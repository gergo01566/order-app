package com.example.onlab

import AppState
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.DialogHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.onlab.navigation.AppNavigation
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderApp () {
    val permissionRequester = PermissionRequester()
    val state = rememberAppState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionRequester.requestPermission(
            context = LocalContext.current,
            permission = android.Manifest.permission.POST_NOTIFICATIONS,
            showRationale = { },
            onPermissionDenied = {
                state.snackBarText = "Permission denied"
            },
            onPermissionGranted = { }
        )
    }

    Scaffold (
        snackbarHost = {
            SnackbarHost(
                hostState = it,
                snackbar = { snackbarData ->
                    Snackbar(snackbarData)
                },
            )
        },
        scaffoldState = state.scaffoldState
    ) { innerPadding ->
        AppNavigation()
        innerPadding.calculateBottomPadding()
//        NavHost(
//            navController = state.navController,
//            //graph = createAppNavGraph(),
//            modifier = Modifier.padding(innerPadding)
//        )
    }
}

@Composable
fun rememberAppState(): AppState {
    val navController = rememberNavController()
    val permissionRequester = PermissionRequester()
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val coroutineScope = rememberCoroutineScope() // Corrected syntax

    return remember {
        AppState(navController = navController, permissionRequester = permissionRequester, scaffoldState = scaffoldState, snackBarText = "Permission denied", coroutineScope = coroutineScope)
    }
}