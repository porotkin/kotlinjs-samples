#!/usr/bin/env python3
import csv
import json
import platform
import re
import shutil
import statistics
import subprocess
import sys
from collections import defaultdict
from contextlib import contextmanager
from datetime import date
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent
VARIANTS = ["composite-builds", "isolated-projects"]
AREAS = ["common", "web", "server", "mobile"]
CORE_SOURCE = "common/core/src/commonMain/kotlin/structure/common/core/CommonGreeting.kt"
IGNORED = {"build", ".gradle", ".kotlin", ".idea", "kotlin-js-store"}
CROSS_PROJECT_API = re.compile(r"\b(includedBuild|projectsEvaluated|evaluationDependsOn|subprojects|allprojects)\b")
LOCAL_GROUPS = ("dev.gradle-structure-test", "isolated-projects")
UNDECLARED_DEPENDENCY = {
    "composite-builds": '"dev.gradle-structure-test.server:database:1.0.0"',
    "isolated-projects": 'project(":server:database")',
}


def files(variant, pattern):
    base = ROOT / variant
    return sorted(p for p in base.rglob(pattern) if p.is_file() and not IGNORED & set(p.relative_to(base).parts))


def non_blank_lines(paths):
    return sum(1 for p in paths for line in p.read_text().splitlines() if line.strip())


def gradle(variant, *args):
    result = subprocess.run(["./gradlew", "--console=plain", *args], cwd=ROOT / variant, capture_output=True, text=True)
    return result.returncode == 0, result.stdout + result.stderr


@contextmanager
def patched(variant, path, transform):
    file = ROOT / variant / path
    original = file.read_text()
    file.write_text(transform(original))
    try:
        yield
    finally:
        file.write_text(original)


def modules(variant):
    base = ROOT / variant
    return [
        ":" + p.parent.relative_to(base).as_posix().replace("/", ":")
        for p in files(variant, "build.gradle.kts")
        if (p.parent / "src").is_dir() and "build-logic" not in p.parts
    ]


def sources(variant):
    base = ROOT / variant
    return {
        p.relative_to(base).as_posix(): p.read_text()
        for p in files(variant, "*")
        if "src" in p.parts and "build-logic" not in p.parts
    }


def module_dir(variant, module):
    return ROOT / variant / module[1:].replace(":", "/")


def fresh_configuration_cache(variant):
    shutil.rmtree(ROOT / variant / ".gradle" / "configuration-cache", ignore_errors=True)


def reported_problems(output):
    match = re.search(r"problems? (?:was|were) found[^\n]*\n((?:- [^\n]*\n)+)", output)
    return sorted(set(match.group(1).splitlines())) if match else []


def jstat(pid, option):
    header, values = subprocess.run(["jstat", option, pid], capture_output=True, text=True).stdout.splitlines()[:2]
    return dict(zip(header.split(), values.split()))


def tasks_executed(output):
    match = re.search(r"(\d+) executed", output)
    return int(match.group(1)) if match else 0


def static_metrics(variant):
    base = ROOT / variant
    scripts = files(variant, "*.gradle.kts") + [p for p in files(variant, "*.kt") if "build-logic" in p.parts]
    properties = files(variant, "gradle.properties")
    script_text = "\n".join(p.read_text() for p in scripts)

    def build_root(area):
        return base / area if (base / area / "settings.gradle.kts").exists() else base

    def effective_properties(area):
        file = build_root(area) / "gradle.properties"
        return file.read_text() if file.exists() else ""

    yield "Gradle support", "Gradle builds (settings files)", len(files(variant, "settings.gradle.kts"))
    yield "Gradle support", "Gradle wrappers", len(files(variant, "gradlew"))
    yield "Gradle support", "Distinct gradle.properties in effect across areas", len({effective_properties(a) for a in AREAS})
    yield "Gradle support", "Build config lines (*.gradle.kts, build-logic, gradle.properties, *.toml)", non_blank_lines(scripts + properties + files(variant, "*.toml"))
    yield "Gradle support", "Generated wiring files committed (projects.versions.toml)", len(files(variant, "projects.versions.toml"))
    yield "IDE support", "Cross-area deps declared via generated catalog (navigates to TOML, not project)", script_text.count("commonProjects.")
    yield "Encapsulation", "Areas buildable/openable as standalone builds", f"{sum(build_root(a) != base for a in AREAS)}/{len(AREAS)}"
    yield "Encapsulation", "Cross-project model access in own build logic (includedBuild, subprojects, ...)", len(CROSS_PROJECT_API.findall(script_text))


def gradle_metrics(variant):
    total = len(modules(variant))

    fresh_configuration_cache(variant)
    ok, out = gradle(variant, "build", "--configuration-cache", "--configuration-cache-problems=fail")
    cache_kb = sum(f.stat().st_size for f in (ROOT / variant / ".gradle" / "configuration-cache").rglob("*") if f.is_file()) // 1024
    yield "Gradle support", "Configuration cache, strict (build)", "✅" if ok else f"❌ {len(reported_problems(out))} problems"
    yield "Build performance", "Configuration cache entry size", f"{cache_kb} KB"

    fresh_configuration_cache(variant)
    ok, out = gradle(variant, "help", "-Dorg.gradle.unsafe.isolated-projects=true", "--configuration-cache-problems=warn")
    problems = reported_problems(out)
    kgp = [p for p in problems if "org.jetbrains.kotlin" in p]
    kgp_plugins = sorted({m.split(".")[-1] for p in kgp for m in re.findall(r"Plugin class '([^']+)'", p)})
    yield "Gradle support", "Isolated Projects enforced (help)", f"{'✅' if ok else '❌'} {len(problems) - len(kgp)} own + {len(kgp)} KGP violations"
    yield "Kotlin support", "KGP plugins violating Isolated Projects", ", ".join(kgp_plugins) or "none"
    fresh_configuration_cache(variant)

    for task in ["clean", "assemble", "check"]:
        _, out = gradle(variant, task, "--dry-run")
        reached = len(re.findall(rf"^:\S+:\S+:{task} SKIPPED$", out, re.M))
        yield "Gradle support", f"Modules reached by `./gradlew {task}`", f"{reached}/{total}"

    _, out = gradle(variant, "build", "--dry-run")
    yield "Kotlin support", "Kotlin/JS npm installs per build (kotlinNpmInstall)", len(re.findall(r"kotlinNpmInstall SKIPPED$", out, re.M))

    for label, transform in [
        ("ABI", lambda s: s + "\nfun abiProbe(): Int = 1\n"),
        ("non-ABI", lambda s: s.replace("Hello,", "Hi,")),
    ]:
        gradle(variant, "build")
        with patched(variant, CORE_SOURCE, transform):
            ok, out = gradle(variant, "build")
        gradle(variant, "build")
        compiled = re.findall(r"^> Task \S+:compile\w*$", out, re.M)
        yield "Kotlin support", f"{label} change in common/core: compile tasks rerun / tasks executed", (
            f"{len(compiled)} / {tasks_executed(out)}" if ok else "❌ build failed"
        )

    _, out = gradle(variant, *[f"{m}:resolveIdeDependencies" for m in modules(variant)], "--no-configuration-cache")
    dependencies = [
        dependency
        for module in modules(variant)
        for file in (module_dir(variant, module) / "build/ide/dependencies/json").glob("*.json")
        for dependency in json.loads(file.read_text())
    ]
    as_module = sum(d.get("type") == "Regular" for d in dependencies)
    as_binary = sum("binaryType" in d and str(d.get("coordinates")).startswith(LOCAL_GROUPS) for d in dependencies)
    yield "IDE support", "Cross-module deps the IDE resolves as source modules / as opaque binaries", f"{as_module} / {as_binary}"

    probe = f'\nkotlin.sourceSets.getByName("jvmMain").dependencies {{ implementation({UNDECLARED_DEPENDENCY[variant]}) }}\n'
    with patched(variant, "common/webapi/build.gradle.kts", lambda s: s + probe):
        verdicts = ["❌ allowed" if gradle(variant, ":common:webapi:compileKotlinJvm")[0] else "✅ blocked"]
        if (ROOT / variant / "common/settings.gradle.kts").exists():
            verdicts.append(("❌ allowed" if gradle(variant, "-p", "common", ":webapi:compileKotlinJvm")[0] else "✅ blocked") + " standalone")
    yield "Encapsulation", "Undeclared common → server dependency", ", ".join(verdicts)

    probe = '\nprintln("visible-projects=${rootProject.allprojects.size}")\n'
    with patched(variant, "server/core/build.gradle.kts", lambda s: s + probe):
        _, out = gradle(variant, ":server:core:help", "--no-configuration-cache")
    yield "Encapsulation", "Projects reachable from server/core build script (rootProject.allprojects)", re.search(r"visible-projects=(\d+)", out).group(1)

    gradle(variant, "--stop")
    gradle(variant, "build", "--no-configuration-cache")
    _, status = gradle(variant, "--status")
    pid = re.search(r"^\s*(\d+)\s+(?:IDLE|BUSY)", status, re.M).group(1)
    yield "Build performance", "Daemon classes loaded after configuring build", jstat(pid, "-class")["Loaded"]
    yield "Build performance", "Daemon metaspace used after configuring build", f"{float(jstat(pid, '-gc')['MU']) / 1024:.0f} MB"
    gradle(variant, "--stop")


def performance_metrics(variant):
    out = HERE / "out" / variant
    shutil.rmtree(out, ignore_errors=True)
    subprocess.run(
        [
            "gradle-profiler", "--benchmark",
            "--project-dir", ROOT / variant,
            "--scenario-file", HERE / "performance.scenarios",
            "--gradle-user-home", Path.home() / ".gradle",
            "--output-dir", out,
        ],
        check=True,
        stdout=subprocess.DEVNULL,
    )
    rows = list(csv.reader((out / "benchmark.csv").open()))
    measured = [row for row in rows if row[0].startswith("measured build")]
    for column, title in enumerate(rows[0][1:], start=1):
        values = [float(row[column]) for row in measured if column < len(row) and row[column]]
        yield "Build performance", f"{title}, median", f"{statistics.median(values) / 1000:.2f} s"


def main():
    expected_modules, expected_sources = modules(VARIANTS[0]), sources(VARIANTS[0])
    for variant in VARIANTS[1:]:
        if modules(variant) != expected_modules or sources(variant) != expected_sources:
            sys.exit(f"Structure parity broken between {VARIANTS[0]} and {variant}")

    results = defaultdict(lambda: defaultdict(dict))
    for variant in VARIANTS:
        measurements = [static_metrics(variant), gradle_metrics(variant)]
        if "--no-perf" not in sys.argv:
            measurements.append(performance_metrics(variant))
        for measurement in measurements:
            for section, metric, value in measurement:
                print(f"[{variant}] {section} / {metric}: {value}", flush=True)
                results[section][metric][variant] = value

    report = [
        "# Composite builds vs isolated projects",
        "",
        f"Generated by `python3 benchmarks/compare.py` (needs gradle-profiler, Android SDK, Xcode) on {date.today()}, {platform.platform()}.",
        f"Structure parity: ✅ {len(expected_modules)} modules, {len(expected_sources)} source files identical.",
        "",
    ]
    for section in ["Build performance", "Gradle support", "Kotlin support", "IDE support", "Encapsulation"]:
        report += [f"## {section}", "", "| Metric | " + " | ".join(VARIANTS) + " |", "|---" * (len(VARIANTS) + 1) + "|"]
        report += [f"| {metric} | " + " | ".join(str(values.get(v, "")) for v in VARIANTS) + " |" for metric, values in results[section].items()]
        report.append("")
    (HERE / "results.md").write_text("\n".join(report))


if __name__ == "__main__":
    main()
