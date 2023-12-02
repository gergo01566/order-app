package com.example.onlab

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.onlab.components.NotificationPermissionRationaleDialog
import kotlin.math.log

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


