package com.example.kmp_mvvm.service

import com.example.kmp_mvvm.model.CountObject
import kotlinx.coroutines.delay

fun interface CounterService {
    suspend fun saveCount(countObject: CountObject, count: Int): Int
}

class FakeCounterService : CounterService {
    override suspend fun saveCount(countObject: CountObject, count: Int): Int {
        delay(500)
        check(count != 13) { "Server refused: 13 ${countObject.name.lowercase()} is bad luck" }
        return count
    }
}
