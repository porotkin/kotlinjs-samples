package kfc.test.jso

import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

class CesiumFunctionWithAdjacentJsPlainObject {
    @Ignore
    @Test
    fun browserEventExternal() = runTest {
        functionWithAdjacentJsPlainObject()

        assertTrue(true)
    }
}
