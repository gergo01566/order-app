package com.example.orderapp.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotificationPermissionRationaleDialog(
    icon: Painter,
    headline: String,
    strapline: String,
    image: Painter,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
) {
    if (!isPermanentlyDeclined) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Image(
                    painter = icon,
                    contentDescription = "Get notified!",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 20.dp, top = 20.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = headline,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Text(
                    textAlign = TextAlign.Center,
                    text = strapline,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Image(
                    painter = image,
                    contentScale = ContentScale.Fit,
                    contentDescription = "Get notified",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Kihagyom", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }

                    TextButton(
                        onClick = { onGoToAppSettingsClick() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Engedélyezem", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            buttons = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Divider()
                    Text(
                        text = "Ugrás a beállításokhoz",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGoToAppSettingsClick() }
                            .padding(16.dp)
                    )
                }
            },
            title = {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Úgy döntöttél, hogy nem engedélyezed az egyes engedélyeket. " +
                            "Lehetőséged van ezt módosítani a beállításokban.",
                    style = MaterialTheme.typography.titleSmall
                )
            },
        )
    }
}