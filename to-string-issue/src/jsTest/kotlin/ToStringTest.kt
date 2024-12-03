package kfc.test.jso

import getArray
import kotlin.test.Test
import kotlin.test.assertEquals

class ToStringTest {
    private val array = getArray()

    @Test
    fun toStringTest() {
        assertEquals(array.toString(), "[1, 2, 3]")
    }

    @Test
    fun smartStringTest() {
        assertEquals("$array", "[1, 2, 3]")
    }

    @Test
    fun listStringTest() {
        assertEquals("${array.toList()}", "[1, 2, 3]")
    }
}
