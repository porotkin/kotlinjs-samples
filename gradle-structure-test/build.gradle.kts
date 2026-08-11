plugins {
    base
}

tasks.wrapper {
    gradleVersion = "9.6.1"
}

tasks.named("build") {
    dependsOn(
        gradle.includedBuild("common").task(":build"),
    )
}
