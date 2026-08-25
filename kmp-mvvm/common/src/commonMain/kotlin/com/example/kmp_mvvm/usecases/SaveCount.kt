package com.example.kmp_mvvm.usecases

import com.example.kmp_mvvm.model.CountObject
import com.example.kmp_mvvm.service.CounterService

class SaveCount(
    private val service: CounterService,
) {
    suspend operator fun invoke(countObject: CountObject, count: Int): Result<Int> {
        countObject.validate(count)?.let { error ->
            return Result.failure(IllegalArgumentException(error))
        }
        return runCatching { service.saveCount(countObject, count) }
    }
}
