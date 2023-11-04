package com.example.onlab.screen.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.onlab.PermissionRequester
import com.example.onlab.components.*
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.screen.product.BasicField
import com.example.onlab.screen.product.ProductButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@Composable
fun CustomerDetailsScreen(
    navigateFromTo:(String, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CustomerDetailsViewModel = hiltViewModel(),
    permissionRequester: PermissionRequester
){
    val uiState by viewModel.uiState

    val changesMade by remember { mutableStateOf(false) }

    val showDialog = remember { mutableStateOf(false) }
    val showNavigationDialog = remember { mutableStateOf(false) }

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
    BackHandler {
        if (changesMade){
            showNavigationDialog.value= true
        } else {
            onNavigateBack()
        }
    }
    when(viewModel.customerResponse){
        is ValueOrException.Loading -> CircularProgressIndicator()
        is ValueOrException.Failure -> CircularProgressIndicator()
        is ValueOrException.Success -> {
            Scaffold(
                topBar = {
                    createTopBar(text = uiState.firstName + " " + uiState.lastName + " adatai", withIcon = true){
                        if(changesMade){
                            showNavigationDialog.value = true
                        }
                        else onNavigateBack()
                    }
                },
                bottomBar = {
                    BottomNavBar(selectedItem = items[1], navigateTo = {
                        navigateFromTo("CustomerDetailsScreen", it)
                    })
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.End
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .statusBarsPadding()
                        .padding(bottom = padding.calculateBottomPadding() / 2),
                    horizontalAlignment = Alignment.Start
                ) {
                    BasicField(text = "Add meg az ügyfél nevét!", label = "Ügyfél keresztneve", value = uiState.firstName , onNewValue = {viewModel.onFirstNameChange(it)})
                    BasicField(text = "Add meg az ügyfél nevét!", label = "Ügyfél vezetékneve", value = uiState.lastName , onNewValue = {viewModel.onLastNameChange(it)})
                    BasicField(text = "Add meg az ügyfél címét!", label = "Ügyfél címe", value = uiState.address , onNewValue = {viewModel.onAddressChange(it)})
                    BasicField(text = "Add meg az ügyfél telefonszámát!", label = "Ügyfél telefonszáma", value = uiState.phoneNumber , onNewValue = {viewModel.onPhoneNumberChange(it)}, keyboardType = KeyboardType.Number)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                            .height(150.dp)
                    ) {
                        ImagePickerButton(onImageSelected = {
                           viewModel.onImageChange(it.toString())
                        }, permissionRequester = permissionRequester)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            AsyncImage(
                                model = uiState.image.toUri(),
                                contentDescription = "profile image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    ProductButton(modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp))
                        .height(40.dp),
                        text = "Ügyfél mentése",
                        enabled = uiState.firstName.isNotEmpty() && uiState.lastName.isNotEmpty() && uiState.address.isNotEmpty(),
                        onClick = {
                            viewModel.onDoneClick(MCustomer(uiState), navigateFromTo)
                        }
                    )

                    ProductButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((10.dp))
                            .height(40.dp),
                        text = "Ügyfél törlése",
                        onClick = {
                            showDialog.value = true
                        },
                        color = Color.Red
                    )
                }
            }
        }
    }

}


