package com.example.orderapp.screens.profile

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.orderapp.common.composables.BottomNavBar
import com.example.orderapp.common.composables.items
import com.example.orderapp.common.composables.ShowConfirmationDialog
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.navigation.DestinationEditProfile
import com.example.orderapp.navigation.DestinationLogin
import com.example.orderapp.navigation.DestinationProfile
import com.example.orderapp.screens.product_details.openAppSettings

@Composable
fun ProfileScreen(
    context: Context,
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateFromTo: (String, String) -> Unit
) {
    Log.d("log", "ProfileScreen: ${viewModel.uiState.value.image}")
    val uiState by viewModel.uiState
    val showDialog = remember { mutableStateOf(false) }

    ShowConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a profilodat?",
        onConfirm = {
            viewModel.onDeleteProfile()
            navigateFromTo("ProfileScreen", DestinationLogin)
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    when(viewModel.userResponse){
        is ValueOrException.Loading -> CircularProgressIndicator()
        is ValueOrException.Failure ->  Unit
        is ValueOrException.Success -> {
            Scaffold(
                topBar = { },
                bottomBar = {
                    BottomNavBar(
                        selectedItem = items[3],
                        navigateTo = {
                            navigateFromTo("ProfileScreen", it)
                        }
                    )
                },
                floatingActionButton = {}
                ,
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.End,
                content = {
                    it.calculateBottomPadding()
                    Column(modifier = Modifier
                        .background(color = androidx.compose.material3.MaterialTheme.colorScheme.surface)
                        .fillMaxSize()
                        .padding(16.dp)) {
                        ProfileInfo(uiState.image,uiState.name, uiState.email, uiState.address)
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 30.dp)
                        )
                        ActionList(
                            onEditClick = {
                                navigateFromTo(DestinationProfile, DestinationEditProfile)
                            },
                            onNotificationClick = {
                                openAppSettings(context as Activity)
                            },
                            onLogoutClick = {
                                viewModel.onLogout()
                                navigateFromTo(DestinationProfile, DestinationLogin)
                            },
                            onDeleteClick = {
                                showDialog.value = true
                            }
                        )
                    }
                })
        }
    }


}

@Composable
@Preview
fun ProfileInfo(imageUri: String = "", userName: String = "", email: String = "", address: String = ""){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUri.toUri(),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentDescription = "profile image",
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = userName,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Email, contentDescription = "Email Icon")

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = email,
        )
    }
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = "Address Icon")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = address,
        )
    }
}

@Composable
@Preview
fun ActionList(
    onEditClick:() -> Unit = {},
    onNotificationClick:() -> Unit = {},
    onLogoutClick:() -> Unit = {},
    onDeleteClick:() -> Unit = {},
){
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Edit,
            contentDescription = "Edit",
            modifier = Modifier
                .clickable {
                    onEditClick()
                }
                .testTag("Edit")
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        ClickableText(
            text = AnnotatedString("Profil szerkesztése"),
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.padding(start = 8.dp),
            onClick = {
                onEditClick()
            }
        )
    }
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Notifications,
            contentDescription = "Notifications",
            modifier = Modifier
                .clickable { /* Handle icon click */ }
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        ClickableText(
            text = AnnotatedString("Értesítések"),
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.padding(start = 8.dp),
            onClick = {
                onNotificationClick()
            }
        )
    }
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.ExitToApp,
            contentDescription = "Log out",
            modifier = Modifier
                .clickable {
                    onLogoutClick()
                }
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        ClickableText(
            text = AnnotatedString("Kijelentkezés"),
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.padding(start = 8.dp),
            onClick = {
                onLogoutClick()
            }
        )
    }

    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Delete,
            contentDescription = "Delete",
            modifier = Modifier
                .clickable { /* Handle icon click */ }
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        ClickableText(
            text = AnnotatedString("Profil törlése"),
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.padding(start = 8.dp),
            onClick = {
                onDeleteClick()
            }
        )
    }

}
