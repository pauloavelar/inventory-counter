package com.pauloavelar.inventory.config

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
    fun main(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
}
