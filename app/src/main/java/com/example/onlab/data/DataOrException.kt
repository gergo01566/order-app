package com.example.onlab.data

data class DataOrException<T, Boolean, E: java.lang.Exception?>(
    var data: T? = null,
    var loading: kotlin.Boolean? = null,
    var e: E? = null
)
