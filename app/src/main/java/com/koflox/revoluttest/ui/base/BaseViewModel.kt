package com.koflox.revoluttest.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.koflox.revoluttest.R
import com.koflox.revoluttest.data.entities.Entity
import com.koflox.revoluttest.data.source.Error
import com.koflox.revoluttest.data.source.Result
import com.koflox.revoluttest.util.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

@Suppress("EXPERIMENTAL_API_USAGE", "MemberVisibilityCanBePrivate", "PropertyName")
abstract class BaseViewModel(
    private val app: Application
) : AndroidViewModel(app), CoroutineScope {

    protected val _networkAvailability = MutableLiveData<Triple<Boolean, String, Int>>()
    val networkAvailability: LiveData<Triple<Boolean, String, Int>> = _networkAvailability

    protected val _userMessage = MutableLiveData<Event<String>>()
    val userMessage: LiveData<Event<String>> = _userMessage

    private val job = Job()
    protected abstract val receiveChannel: ReceiveChannel<Result<Entity, Error>>

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    abstract fun handleResult(result: Result<Entity, Error>)

    init {
        processStream()
    }

    private fun processStream() {
        launch {
            receiveChannel.consumeEach {
                handleResult(it)
            }
        }
    }

    override fun onCleared() {
        receiveChannel.cancel()
        coroutineContext.cancel()
        super.onCleared()
    }

    fun onNetworkAvailabilityChanged(isAvailable: Boolean) {
        _networkAvailability.value = when {
            isAvailable -> Triple(isAvailable, "", -1)
            else -> Triple(isAvailable, app.getString(R.string.text_no_network), Snackbar.LENGTH_INDEFINITE)
        }
    }

}