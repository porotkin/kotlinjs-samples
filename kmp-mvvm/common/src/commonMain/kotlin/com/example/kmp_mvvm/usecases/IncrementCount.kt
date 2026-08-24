package com.example.kmp_mvvm.usecases

class IncrementCount {
    operator fun invoke(count: Int): Int =
        count + 1
}
