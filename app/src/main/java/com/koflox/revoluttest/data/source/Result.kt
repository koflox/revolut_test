package com.koflox.revoluttest.data.source

sealed class Result<out T, out R> {

    class Success<out T>(val data: T) : Result<T, Nothing>()

    class Failure<out R : Error>(val error: R) : Result<Nothing, R>()

    sealed class State<out T> : Result<T, Nothing>() {
        class Loading<out T>(val stub: T) : State<T>()
        class Loaded<out T>(val stub: T) : State<T>()
    }

    fun handle(
        onSuccess: (T) -> Unit = {},
        onFailure: (R) -> Unit = {},
        onLoad: (State<T>) -> Unit = {}
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is Failure -> onFailure(error)
            is State -> onLoad(this)
        }
    }

}

sealed class Error {
    object NetworkError : Error()
}

fun Result<*, *>.isSucceeded() = this is Result.Success && data != null