@file:OptIn(ExperimentalMaterialApi::class)

package com.example.onlab

import com.example.onlab.components.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.onlab.navigation.AppNavigation
import com.example.onlab.ui.theme.OnlabTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionRequester = PermissionRequester()

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications")
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotificatio")

        setContent {
            val scope = rememberCoroutineScope()
            val shouldShowRequestPermissionRational: Boolean = shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)
            val openAlertDialog = remember { mutableStateOf(false) }
            val context = LocalContext.current
            OrderApp()
//            OnlabTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//
////                    LaunchedEffect(key1 = shouldShowRequestPermissionRational, block = {
////                        permissionRequester.requestPermission(
////                            context = context,
////                            permission = android.Manifest.permission.POST_NOTIFICATIONS,
////                            showRationale = {
////                                Log.d("show", "onCreate: meghivv")
////                                openAlertDialog.value = true
////                            },
////                            onPermissionDenied = {}
////                        ) {
////                            //requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
////                        }
////                    })
//
//                    AppNavigation()
//
//
//
//            }
//        }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OnlabTheme {
        AppNavigation()
        }
}

