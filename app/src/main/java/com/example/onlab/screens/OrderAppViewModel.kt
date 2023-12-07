package com.example.onlab.screens

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class OrderAppViewModel: ViewModel() {
    fun launchCatching(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Log.d("Error", throwable.message.orEmpty())
            },
            block = block
        )

    val permissionsToAsk = mutableStateListOf<String>()

    fun dismissDialog() {
        permissionsToAsk.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !permissionsToAsk.contains(permission)) {
            permissionsToAsk.add(permission)
        }
    }

}