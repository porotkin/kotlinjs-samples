package structure.server.app

import structure.server.core.serverCoreMessage
import structure.server.database.databaseStatus

fun serverAppStatus(): String =
    "${serverCoreMessage()} | ${databaseStatus()}"
