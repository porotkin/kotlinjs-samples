package kfc.test.jso

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CesiumFunctionWithAdjacentJsPlainObject {
    @Test
    fun browserEventExternal() = runTest {
        functionWithAdjacentJsPlainObject()

        assertTrue(true)
    }
}
