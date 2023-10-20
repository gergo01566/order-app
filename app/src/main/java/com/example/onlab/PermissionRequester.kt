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
import kotlin.math.log

class PermissionRequester {

    fun requestPermission(
        context: Context,
        permission: String,
        showRationale: () -> Unit,
        onPermissionDenied: () -> Unit,
        onPermissionGranted: () -> Unit
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
                showRationale()
            }
            else -> {
                Log.d("show", "requestPermission: else")
                ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), 1)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun NotificationPermissionRationaleDialog(
        icon: Painter,
        headline: String,
        strapline: String,
        image: Painter,
        onSkip: () -> Unit = {},
        onConfirm: () -> Unit = {}
    ){

        Dialog(
            onDismissRequest = { onSkip() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Log.d("show", "NotificationPermissionRationaleDialog: show")
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Small icon-sized image
                    Image(
                        painter = icon,
                        contentDescription = "Get notified!",
                        modifier = Modifier.size(50.dp) // Adjust the size as needed
                    )

                    androidx.compose.material3.Text(
                        modifier = Modifier.padding(bottom = 20.dp, top = 20.dp),
                        text = headline,
                        textAlign = TextAlign.Start,
                        letterSpacing = 2.sp,
                        style = MaterialTheme.typography.displayMedium,
                    )

                    androidx.compose.material3.Text(
                        text = strapline,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(15.dp)) // Add space between the big image and buttons

                    // Big image (filling available height)
                    Image(
                        painter = image,
                        contentScale = ContentScale.Fit,
                        contentDescription = "Get notified",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(15f)// Adjust the fraction to control height
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Add space between the big image and buttons

                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { onSkip() },
                            modifier = Modifier.weight(1f) // Makes the button take half the available width
                        ) {
                            androidx.compose.material3.Text("Kihagyom", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }

                        TextButton(
                            onClick = { onConfirm() },
                            modifier = Modifier.weight(1f) // Makes the button take half the available width
                        ) {
                            androidx.compose.material3.Text("Lássuk", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
        }
    }

}


