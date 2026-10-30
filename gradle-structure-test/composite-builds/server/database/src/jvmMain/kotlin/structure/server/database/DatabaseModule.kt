package structure.server.database

import structure.server.core.serverCoreMessage

fun databaseStatus(): String =
    "database ready for ${serverCoreMessage()}"
