package kfc.test.jso

import web.dom.document
import web.html.HTML.div
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

class CesiumTest {
    @Ignore
    @Test
    fun simple() {
        val container = document.createElement(div)
        val viewer = CesiumViewer(container)

        assertNotNull(viewer)
    }
}
