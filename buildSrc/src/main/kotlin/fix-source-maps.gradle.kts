val fixProjectSourceMaps = tasks.register<FixProjectSourceMapsTask>("fixProjectSourceMaps")
val fixSyncedSourceMaps = tasks.register<FixSyncedSourceMapsTask>("fixSyncedSourceMaps")

// For production tasks only
tasks.named(compileTasks.last()) {
    finalizedBy(fixProjectSourceMaps)
}

tasks.named(compileSyncTasks.last()) {
    dependsOn(fixProjectSourceMaps)

    finalizedBy(fixSyncedSourceMaps)
}
