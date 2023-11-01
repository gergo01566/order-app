package com.example.onlab.data

data class DataOrException<T, Boolean, E: java.lang.Exception?>(
    var data: T? = null,
    var loading: Boolean? = null,
    var e: E? = null
)

sealed class ValueOrException<out T> {
    object Loading: ValueOrException<Nothing>()

    data class Success<out T>(
        val data: T
    ): ValueOrException<T>()

    data class Failure(
        val e: Exception?
    ): ValueOrException<Nothing>()
}
