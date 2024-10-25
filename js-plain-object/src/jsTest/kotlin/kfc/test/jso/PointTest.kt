package kfc.test.jso

import kotlin.test.Test
import kotlin.test.assertEquals

class PointTest {
    @Test
    fun simple() {
        val point = PointInit(
            Point(
                Point.ConstructorOptions(
                    x = 4.0,
                    y = 5.0,
                )
            )
        )

        assertEquals(4.0, point.getX())
        assertEquals(5.0, point.getY())
    }
}
