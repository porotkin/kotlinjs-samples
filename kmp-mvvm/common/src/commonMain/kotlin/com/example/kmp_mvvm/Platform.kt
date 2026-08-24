package com.example.kmp_mvvm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform