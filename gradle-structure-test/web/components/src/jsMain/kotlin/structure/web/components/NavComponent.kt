package structure.web.components

import structure.common.webapi.webApiPath
import structure.web.common.webCommonTitle

fun navComponent(): String =
    "${webCommonTitle()} -> ${webApiPath("navigation")}"
