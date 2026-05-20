package structure.common.webapi

import structure.common.client.sharedClientMessage

fun webApiPath(resource: String): String =
    "/api/$resource?label=${sharedClientMessage(resource)}"
