package com.koflox.revoluttest.util

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

fun CoroutineScope.startCoroutineTimer(
    delayMillis: Long = 0, repeatMillis: Long = 0,
    dispatcher: CoroutineDispatcher = Dispatchers.Default, block: () -> Unit
): Job = launch(dispatcher) {
    delay(delayMillis)
    when {
        repeatMillis > 0 -> {
            while (true) {
                block()
                delay(repeatMillis)
            }
        }
        else -> block()
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
fun <T> CoroutineScope.mergeChannels(vararg channels: ReceiveChannel<T>): ReceiveChannel<T> {
    return produce {
        channels.forEach {
            launch { it.consumeEach { send(it) } }
        }
    }
}