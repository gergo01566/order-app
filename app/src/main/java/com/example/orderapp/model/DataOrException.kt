package com.example.orderapp.model

sealed class ValueOrException<out T> {
    object Loading: ValueOrException<Nothing>()

    data class Success<out T>(
        val data: T
    ): ValueOrException<T>()

    data class Failure(
        val e: Exception?
    ): ValueOrException<Nothing>()
}
