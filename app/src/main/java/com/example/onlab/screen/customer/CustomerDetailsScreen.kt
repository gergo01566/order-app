package com.example.onlab.screen.customer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.example.onlab.screen.profile.ProfileImage
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlab.R

import com.example.onlab.components.*
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Customer
import com.example.onlab.screen.product.BasicField
import com.example.onlab.screen.product.openAppSettings
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun CustomerDetailsScreen(
    context: Context,
    navigateFromTo:(String, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CustomerDetailsViewModel = hiltViewModel(),
){
    val uiState by viewModel.uiState
    val showDialog = remember { mutableStateOf(false) }
    val showNavigationDialog = remember { mutableStateOf(false) }

    val filesPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(
                permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                isGranted = isGranted
            )
        }
    )

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri!=null) viewModel.onImageChange(uri.toString())
        }
    )

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Ügyfél szerkesztése") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(
                        onClick = {
                            viewModel.onNavigateBack({
                                showNavigationDialog.value = true
                            }){
                                onNavigateBack()
                            }
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.IconButton(
                        enabled = uiState.firstName.isNotEmpty() && uiState.lastName.isNotEmpty() && uiState.address.isNotEmpty(),
                        onClick = {
                            viewModel.onDoneClick(Customer(uiState), navigateFromTo)
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Done,
                            contentDescription = "Save"
                        )
                    }
                    androidx.compose.material3.IconButton(
                        onClick = {
                            showDialog.value = true
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .fillMaxHeight()
                .statusBarsPadding()
                .padding(bottom = paddingValues.calculateBottomPadding() / 2),
            horizontalAlignment = Alignment.Start
        ){
            Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))
            ProfileImage(uiState.image.toUri()) {
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    singlePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                filesPermissionResultLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            CustomerData(
                customerResponse = viewModel.customerResponse,
                uiState = uiState,
                onFirstNameChange = { viewModel.onFirstNameChange(it) },
                onLastNameChange = { viewModel.onLastNameChange(it) },
                onAddressChange = { viewModel.onAddressChange(it) },
                onPhoneNumberChange = { viewModel.onPhoneNumberChange(it) },
            )
            UpdateCustomer(updateCustomerResponse = viewModel.updateCustomerResponse)
            AddCustomer(addCustomerResponse = viewModel.addCustomerResponse)
            DeleteCustomer(deleteCustomerResponse = viewModel.deleteCustomerResponse)
            BackHandler {
                viewModel.onNavigateBack({
                    showNavigationDialog.value = true
                }){
                    onNavigateBack()
                }
            }
            viewModel.permissionsToAsk.forEach{ permission ->
                when(permission){
                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        NotificationPermissionRationaleDialog(
                            icon = painterResource(id = R.drawable.cloud_storage),
                            headline = "Tölts fel képeket!",
                            strapline = "A termékekről és ügyfelekről képeket tölthetsz fel, így később könnyebben megtalálod őket." +
                                    "Sajnos úgy döntöttél, hogy nem engedélyezed a fájlok elérését. " +
                                    "Sajnos enélkül nem lesz lehetőséged képeket feltölteni",
                            image = painterResource(id = R.drawable.gallery),
                            isPermanentlyDeclined = ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                            },
                            onGoToAppSettingsClick = {
                                openAppSettings(context)
                                viewModel.dismissDialog()
                            }
                        )
                    }
                }
            }
        }
    }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd az ügyfelet?",
        onConfirm = {
            viewModel.onDeleteCustomer(uiState.id, navigateFromTo = navigateFromTo)
            showDialog.value = false
        },
        onDismiss = {
            showDialog.value = false
        }
    )

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
fun UpdateCustomer(
    updateCustomerResponse: ValueOrException<Boolean>
){
    when(updateCustomerResponse){
        is ValueOrException.Loading -> LoadingScreen()
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> print(updateCustomerResponse.e)
    }
}

@Composable
fun AddCustomer(
    addCustomerResponse: ValueOrException<Boolean>
){
    when(addCustomerResponse){
        is ValueOrException.Loading -> LoadingScreen()
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> print(addCustomerResponse.e)
    }
}

@Composable
fun DeleteCustomer(
    deleteCustomerResponse: ValueOrException<Boolean>
){
    when(deleteCustomerResponse){
        is ValueOrException.Loading -> LoadingScreen()
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> print(deleteCustomerResponse.e)
    }
}

@Composable
fun CustomerData(
    customerResponse: ValueOrException<Customer>,
    uiState: CustomerUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
){
    when(customerResponse) {
        is ValueOrException.Loading -> CircularProgressIndicator()
        is ValueOrException.Failure -> Unit
        is ValueOrException.Success -> {
            BasicField(
                text = "Add meg az ügyfél nevét!",
                label = "Ügyfél keresztneve",
                value = uiState.firstName,
                isError = !ValidationUtils.inputContainsOnlyLetter(uiState.firstName),
                onNewValue = { onFirstNameChange(it) })
            BasicField(
                text = "Add meg az ügyfél nevét!",
                label = "Ügyfél vezetékneve",
                value = uiState.lastName,
                isError = !ValidationUtils.inputContainsOnlyLetter(uiState.lastName),
                onNewValue = { onLastNameChange(it) })
            BasicField(
                text = "Add meg az ügyfél címét!",
                label = "Ügyfél címe",
                value = uiState.address,
                isError = !ValidationUtils.inputIsNotEmpty(uiState.address),
                onNewValue = { onAddressChange(it) })
            BasicField(
                text = "Add meg az ügyfél telefonszámát!",
                label = "Ügyfél telefonszáma",
                value = uiState.phoneNumber,
                isError = !ValidationUtils.inputContaintsOnlyNumbers(uiState.phoneNumber),
                onNewValue = { onPhoneNumberChange(it) },
                keyboardType = KeyboardType.Number
            )
        }
    }
}