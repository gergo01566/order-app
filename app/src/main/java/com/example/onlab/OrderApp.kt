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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.onlab.components.SnackbarManager
import com.example.onlab.navigation.DestinationCustomerList
import com.example.onlab.navigation.DestinationLogin
import com.example.onlab.navigation.appNavigation
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderApp () {
    val permissionRequester = PermissionRequester()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionRequester.requestPermission(
            context = LocalContext.current,
            permission = android.Manifest.permission.POST_NOTIFICATIONS,
            showRationale = { },
            onPermissionDenied = {
                //state.snackBarText = "Permission denied"
            },
            onPermissionGranted = { }
        )
    }

    Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary) {
        val appState = rememberAppState()

        Scaffold (
            snackbarHost = {
                SnackbarHost(
                    hostState = it,
                    snackbar = { snackbarData ->
                        Snackbar(snackbarData)
                    },
                )
            },
            scaffoldState = appState.scaffoldState
        ) { innerPadding ->
            NavHost(
                navController = appState.navController,
                startDestination = if(FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
                    DestinationLogin
                } else {
                    DestinationCustomerList
                },
                modifier = Modifier.padding(innerPadding)
            ){
                appNavigation(appState)
            }
        }
        
    }

}

@Composable
fun rememberAppState(): AppState {
    val navController = rememberNavController()
    val permissionRequester = PermissionRequester()
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val coroutineScope = rememberCoroutineScope()
    val snackbarManager = SnackbarManager

    return remember {
        AppState(navController = navController, permissionRequester = permissionRequester, scaffoldState = scaffoldState, snackbarManager = snackbarManager, coroutineScope = coroutineScope)
    }
}