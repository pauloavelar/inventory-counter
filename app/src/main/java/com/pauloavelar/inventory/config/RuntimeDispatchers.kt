package com.pauloavelar.inventory.config

import kotlinx.coroutines.Dispatchers

class RuntimeDispatchers : CoroutineDispatchers {
    override fun main() = Dispatchers.Main
    override fun io() = Dispatchers.IO
}
