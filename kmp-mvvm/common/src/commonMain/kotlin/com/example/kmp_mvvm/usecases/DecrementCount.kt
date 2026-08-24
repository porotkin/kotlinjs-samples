package com.example.kmp_mvvm.usecases

class DecrementCount {
    operator fun invoke(count: Int): Int =
        count - 1
}
