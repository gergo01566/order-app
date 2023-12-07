package com.example.onlab

import android.content.Context
import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.example.onlab.common.snackbar.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class AppState(
    val navController: NavHostController,
    val scaffoldState: ScaffoldState,
    val context: Context,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessage.filterNotNull().collect { message ->
                val text = resources.getString(message)
                scaffoldState.snackbarHostState.showSnackbar(text)
                snackbarManager.clearSnackbarState()
            }
        }
    }

    fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

