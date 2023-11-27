package com.example.onlab.components

import io.grpc.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarManager {
    private val messages: MutableStateFlow<String?> = MutableStateFlow(null)
    val snackbarMessage: StateFlow<String?>
        get() = messages.asStateFlow()

    fun displayMessage(message: Int){
        messages.value = message.toString()
    }

    fun clearSnackbarState(){
        messages.value = null
    }
}