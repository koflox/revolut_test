package com.koflox.revoluttest.use_cases

import com.koflox.revoluttest.data.entities.Entity
import com.koflox.revoluttest.data.source.Error
import com.koflox.revoluttest.data.source.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.CoroutineContext

abstract class BaseUseCase<in Data> : CoroutineScope {

    private val parentJob = SupervisorJob()
    private val mainDispatcher = Dispatchers.Main
    private val backgroundDispatcher = Dispatchers.Default
    protected val resultChannel = Channel<Result<Entity, Error>>()

    val receiveChannel: ReceiveChannel<Result<Entity, Error>> = resultChannel

    private var currentJob: Job? = null

    protected abstract suspend fun run(data: Data)

    override val coroutineContext: CoroutineContext
        get() = parentJob + mainDispatcher

    fun invoke(params: Data, isSync: Boolean) {
        currentJob = launch {
            when {
                isSync -> run(params)
                else -> withContext(backgroundDispatcher) {
                    run(params)
                }
            }
        }
    }

    protected fun <T> startAsync(block: suspend () -> T): Deferred<T> = async(parentJob) {
        block()
    }

    fun clear() {
        resultChannel.close()
        parentJob.cancel()
    }

}