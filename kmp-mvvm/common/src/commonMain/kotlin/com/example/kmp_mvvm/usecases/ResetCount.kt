package com.example.kmp_mvvm.usecases

class ResetCount(private val initialCount: Int = 0) {
    operator fun invoke(): Int =
        initialCount
}
