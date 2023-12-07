package com.example.orderapp.screens.edit_profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.orderapp.common.composables.DismissChangesDialog
import com.example.orderapp.common.utils.ValidationUtils
import com.example.orderapp.model.User
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.screens.customers.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    applicationContext: Context,
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    navigateFromTo:(String, String) -> Unit,
) {
    val showNavigationDialog = remember { mutableStateOf(false) }
    val uiState by viewModel.uiState

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri!=null) viewModel.onImageChange(uri.toString())
        }
    )

    val filesPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(
                permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                isGranted = isGranted
            )
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                title = {
                    Text("Profil szerkesztése", color = MaterialTheme.colorScheme.onPrimary)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onNavigateBack({
                                showNavigationDialog.value = true
                            }){
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(
                        enabled = viewModel.isValidProfileInputs(),
                        onClick = { viewModel.onUpdateUser(navigateFromTo = navigateFromTo) }) {
                        Icon(Icons.Default.Done, contentDescription = "Save", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) {
        it.calculateBottomPadding()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(it.calculateTopPadding()))
            UserDataList(
                userResponse = viewModel.userResponse,
                onClick = {
                    if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        singlePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    filesPermissionResultLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                },
                uiState =  uiState,
                onNameChange = { name -> viewModel.onNameChange(name)},
                onAddressChange = { address -> viewModel.onAddressChange(address)},
                paddingValue = it
            )
            UpdateUser(updateUserResponse = viewModel.updateUserResponse)
        }
    }
        if(showNavigationDialog.value){
            DismissChangesDialog(onDismiss = {
                showNavigationDialog.value = false
            }) {
                onNavigateBack()
                showNavigationDialog.value = false
            }
        }
    }

@Composable
fun UpdateUser(
    updateUserResponse: ValueOrException<Boolean>
){
    when(updateUserResponse) {
        is ValueOrException.Loading -> {
            LoadingScreen()
        }
        is ValueOrException.Failure -> Unit
        is ValueOrException.Success -> Unit
    }
}

@Composable
fun UserDataList(
    userResponse: ValueOrException<User>,
    uiState: ProfileUiState,
    onNameChange:(String)->Unit,
    onAddressChange:(String)->Unit,
    onClick: () -> Unit,
    paddingValue: PaddingValues
){
    when(userResponse) {
        is ValueOrException.Loading -> {
            LoadingScreen()
        }
        is ValueOrException.Failure -> Unit
        is ValueOrException.Success -> {
            ProfileImage(imageUri = uiState.image.toUri()) {
                onClick()
            }
            Spacer(modifier = Modifier.height(paddingValue.calculateStartPadding(LayoutDirection.Ltr)))
            EditTextField(

                value = uiState.name,
                isError = !ValidationUtils.inputIsNotEmpty(uiState.name),
                label = "Név"
            ) { onNameChange(it) }
            EditTextField(
                value = uiState.address,
                isError = !ValidationUtils.inputIsNotEmpty(uiState.address),
                label = "Cím"
            ) { onAddressChange(it) }
            EditTextField(value = uiState.email, label = "Email", readOnly = true) { }
        }
    }
}

@Composable
fun ProfileImage(imageUri: Uri,onClick:() -> Unit) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        AsyncImage(
            model = imageUri,
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,            // crop the image if it's not a square
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)                       // clip to the circle shape
                .border(2.dp, Color.Gray, CircleShape)   // add a border (optional)
                .padding(5.dp)
                .clickable {
                    onClick()
                }
        )
        Text(modifier = Modifier
            .clickable { onClick() }
            .padding(5.dp), text = "Profilkép módosítása")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextField(value: String, label: String, readOnly: Boolean = false, isError: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        isError = isError,
        value = value,
        readOnly = readOnly,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(56.dp)
            .testTag(label)
    )
}
