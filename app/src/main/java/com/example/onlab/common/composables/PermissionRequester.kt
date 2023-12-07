package com.example.onlab.common.composables

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionRequester {
    @Composable
    fun requestPermission(
        context: Context,
        permission: String,
        showRationale: () -> Unit,
        onPermissionDenied: () -> Unit,
        onPermissionGranted: () -> Unit,
        showDialog: MutableState<Boolean> = remember {
            mutableStateOf(false)
        }
    ){
        when{
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                //Permission granted
                Log.d("show", "requestPermission: granted")
                onPermissionGranted()
            }
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED -> {
                //Permission denied
                Log.d("show", "requestPermission: denied")
                onPermissionDenied()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission) -> {
                Log.d("show", "requestPermission: show")
                showDialog.value = true
            }
            else -> {
                Log.d("show", "requestPermission: else")
                ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), 1)
            }
        }

        when {
//            showDialog.value -> {
//                Log.d("show", "requestPermission: showDialog")
//                NotificationPermissionRationaleDialog(
//                    icon = painterResource(id = R.drawable.active),
//                    headline = "Értesülj!",
//                    strapline = "Küldj és fogadj értesítéseket az új rendelésekről.",
//                    image = painterResource(id = R.drawable.get_notified),
//                    onSkip = { showDialog.value = false },
//                    isPermanentlyDeclined = true,
//                    onClick = {},
//                    onConfirm = {
//                        ActivityCompat.requestPermissions(this@PermissionRequester as Activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
//                    }
//                )
//            }
        }
    }


}


