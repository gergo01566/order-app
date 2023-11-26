package com.example.onlab.screen.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.onlab.components.BottomNavBar
import com.example.onlab.components.createTopBar
import com.example.onlab.components.items
import com.example.onlab.components.showConfirmationDialog
import com.example.onlab.data.ValueOrException
import com.example.onlab.navigation.DestinationEditProfile
import com.example.onlab.navigation.DestinationLogin
import com.example.onlab.navigation.DestinationProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateFromTo: (String, String) -> Unit
) {
    val uiState by viewModel.uiState
    val showDialog = remember { mutableStateOf(false) }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a profilodat?",
        onConfirm = {
            viewModel.onDeleteProfile()
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
                                //TODO
                            },
                            onLogoutClick = {
                                //TODO
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
            contentDescription = "Notifications",
            modifier = Modifier
                .clickable {
                    onEditClick()
                }
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

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileOption(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        onClick = onClick
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}
