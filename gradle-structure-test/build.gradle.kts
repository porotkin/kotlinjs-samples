plugins {
    base
}

tasks.wrapper {
    gradleVersion = "9.8.2"
}

tasks.named("build") {
    dependsOn(
        gradle.includedBuild("common").task(":build"),
        gradle.includedBuild("web").task(":build"),
        gradle.includedBuild("server").task(":build"),
    )
}
