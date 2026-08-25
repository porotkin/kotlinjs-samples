package com.example.kmp_mvvm

import com.example.kmp_mvvm.viewmodel.DependencyTracker
import com.example.kmp_mvvm.viewmodel.signal
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals

class SignalTrackingConcurrencyTest {

    @Test
    fun trackingIsThreadLocal() {
        val first = signal(1)
        val second = signal(2)
        val bothTracking = CountDownLatch(2)

        lateinit var firstReads: Set<Any?>
        lateinit var secondReads: Set<Any?>

        val firstThread = thread {
            val (_, reads) = DependencyTracker.track {
                bothTracking.countDown()
                bothTracking.await()
                first.value
            }
            firstReads = reads.keys
        }
        val secondThread = thread {
            val (_, reads) = DependencyTracker.track {
                bothTracking.countDown()
                bothTracking.await()
                second.value
            }
            secondReads = reads.keys
        }
        firstThread.join()
        secondThread.join()

        assertEquals(setOf<Any?>(first), firstReads)
        assertEquals(setOf<Any?>(second), secondReads)
    }
}
