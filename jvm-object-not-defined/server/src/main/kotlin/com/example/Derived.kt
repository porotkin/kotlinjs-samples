package com.example

abstract class AbstractBase(
    override val id: String,
) : Base

object Derived : AbstractBase("Derived")
