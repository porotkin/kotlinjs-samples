package kfc.test.jso

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

class ExternalEventTest {
    @Ignore
    @Test
    fun browserEventExternal() {
        assertNotNull(
            ExternalEvent("myEventType")
        )
    }
}
