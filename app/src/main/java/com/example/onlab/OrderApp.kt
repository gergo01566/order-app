package com.example.onlab

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.onlab.common.snackbar.SnackbarManager
import com.example.onlab.navigation.DestinationCustomerList
import com.example.onlab.navigation.DestinationLogin
import com.example.onlab.navigation.appNavigation
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderApp () {
    val appState = rememberAppState()


    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(ContextCompat.checkSelfPermission(appState.context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        } else {
            mutableStateOf(true)
        }
    }

    Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary) {
        val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {
            hasNotificationPermission = it
        } )

        if (!hasNotificationPermission && ActivityCompat.shouldShowRequestPermissionRationale(appState.context as Activity,Manifest.permission.POST_NOTIFICATIONS)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val coroutineScope = rememberCoroutineScope()
    val snackbarManager = SnackbarManager
    val context = LocalContext.current
    val resources = LocalContext.current.resources

    return remember {
        AppState(navController = navController, scaffoldState = scaffoldState, snackbarManager = snackbarManager, coroutineScope = coroutineScope, resources = resources, context = context)
    }
}