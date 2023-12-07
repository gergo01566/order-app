package com.example.onlab.common.snackbar

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarManager {
    private val messages: MutableStateFlow<Int?> = MutableStateFlow(null)
    val snackbarMessage: StateFlow<Int?>
        get() = messages.asStateFlow()

    fun displayMessage(@StringRes message: Int){
        messages.value = message
    }

    fun clearSnackbarState(){
        messages.value = null
    }
}

