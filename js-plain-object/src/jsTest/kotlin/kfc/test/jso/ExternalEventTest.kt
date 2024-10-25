package kfc.test.jso

import kotlin.test.Test
import kotlin.test.assertNotNull

class ExternalEventTest {
    @Test
    fun browserEventExternal() {
        assertNotNull(
            ExternalEvent("myEventType")
        )
    }
}
