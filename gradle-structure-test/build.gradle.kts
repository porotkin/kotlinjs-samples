plugins {
    base
}

tasks.named("build") {
    dependsOn(
        gradle.includedBuild("common").task(":build"),
        gradle.includedBuild("mobile").task(":build"),
        gradle.includedBuild("web").task(":build"),
        gradle.includedBuild("server").task(":build"),
    )
}
