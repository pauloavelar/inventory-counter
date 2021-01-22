package com.pauloavelar.inventory.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class AppScope : KoinComponent {
    private val scope: CoroutineScope by inject()
    private val dispatchers: CoroutineDispatchers by inject()

    fun runOnIoThread(block: suspend CoroutineScope.() -> Unit) {
        scope.launch(dispatchers.io(), block = block)
    }

    fun runOnUiThread(block: suspend CoroutineScope.() -> Unit) {
        scope.launch(dispatchers.main(), block = block)
    }
}
