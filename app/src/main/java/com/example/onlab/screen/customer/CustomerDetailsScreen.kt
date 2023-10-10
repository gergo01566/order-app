package com.example.onlab.screen.customer

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.data.DataOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.screen.product.ProductButton
import com.example.onlab.viewModels.MCustomerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@ExperimentalPermissionsApi
@Composable
fun CustomerDetailsScreen(navController: NavController, customerID: String? = null, customerViewModel: MCustomerViewModel){

    val contextForToast = LocalContext.current.applicationContext

    var buttonEnabled by remember { mutableStateOf(false) }

    var changesMade by remember { mutableStateOf(false) }

    var customer by remember {
        mutableStateOf(customerViewModel.data.value.data?.first{ mCustomer ->
            Log.d("customerID", "CustomerDetailsScreen: $customerID")
            mCustomer.id == customerID.toString()
        })
    }

    val customerList = produceState<DataOrException<List<MCustomer>,
            Boolean, java.lang.Exception>>(initialValue = DataOrException(data = emptyList(), true, Exception(""))){
            value = customerViewModel.data.value
    }.value

    var imageUri by remember {
        mutableStateOf<Uri?>(customer!!.image.toUri())
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    val showDialog = remember { mutableStateOf(false) }
    val showNavigationDialog = remember { mutableStateOf(false) }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd az ügyfelet?",
        onConfirm = {
            customerViewModel.deleteCustomer(customerID){
                showDialog.value = false
                navController.navigate(route = "CustomerScreen")
                Toast.makeText(contextForToast, "Ügyfél törölve", Toast.LENGTH_SHORT).show()
                customerViewModel.getAllCustomersFromDatabase()
            }
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    if(showNavigationDialog.value){
        DismissChangesDialog(onDismiss = {
            showNavigationDialog.value = false
        }) {
            navController.popBackStack()
            showNavigationDialog.value = false
        }
    }
    BackHandler {
        if (changesMade){
            showNavigationDialog.value= true
        } else {
            navController.popBackStack()
        }
    }
    Scaffold(
        topBar = {
            createTopBar(navController = navController, text = customer!!.firstName + " " + customer!!.lastName + " adatai", withIcon = true){
                if(changesMade){
                    showNavigationDialog.value = true
                }
                else navController.popBackStack()
            }
            if(customerList.loading == true){
                LinearProgressIndicator()
                customerList.loading = false
            }
        },
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController, selectedItem = items[1])
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
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp)),
                maxLines = 1,
                value = customer?.firstName ?: "",
                onValueChange = { newValue ->
                    if (newValue.length <= 20) {
                        customer = customer?.copy(firstName = newValue) ?: customer
                        changesMade = true // Mark changes as made
                    }
                    else if(newValue.isEmpty()) buttonEnabled = false
                    else Toast.makeText(
                        contextForToast,
                        "A keresztnév max. 20 karakter lehet",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                label = { Text(text = "Ügyfél keresztneve") },
                placeholder = { Text(text = "Add meg az ügyfél nevét!") }
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp)),
                value = customer?.lastName ?: "",
                maxLines = 1,
                onValueChange = { newValue ->
                    if (newValue.length <= 20){
                        customer = customer?.copy(lastName = newValue) ?: customer
                        changesMade = true // Mark changes as made
                    }
                    else if(newValue.isEmpty()) buttonEnabled = false
                    else Toast.makeText(
                        contextForToast,
                        "A vezetéknév max. 20 karakter lehet",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                label = { Text(text = "Ügyfél vezetékneve") },
                placeholder = { Text(text = "Add meg az ügyfél nevét!") }
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp)),
                value = customer?.address ?: "",
                onValueChange = { newValue ->
                    if (newValue.length <= 30) {
                        customer = customer?.copy(address = newValue) ?: customer
                        changesMade = true // Mark changes as made
                    }
                    else Toast.makeText(
                        contextForToast,
                        "A cím max. 30 karakter lehet",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                label = { Text(text = "Ügyfél címe") },
                placeholder = { Text(text = "Add meg az ügyfél címét!") }
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp)),
                value = customer?.phoneNumber ?: "",
                onValueChange = { newValue ->
                    if (newValue.length <= 15){
                        customer = customer?.copy(phoneNumber = newValue) ?: customer
                        changesMade = true // Mark changes as made
                    }
                    else Toast.makeText(
                        contextForToast,
                        "A telefonszám max. 15 számjegyű lehet",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                label = { Text(text = "Ügyfél telefonszáma") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                placeholder = { Text(text = "Add meg az ügyfél telefonszámát!") }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                    .height(150.dp)
            ) {
                ImagePickerButton(onImageSelected = {
                    permissionsState.launchMultiplePermissionRequest()
                    imageUri = it
                    customer = customer!!.copy(image = it.toString())
                })
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    AsyncImage(
                        model = customer?.image,
                        contentDescription = "profile image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            buttonEnabled = customer!!.firstName.isNotEmpty() && customer!!.lastName.isNotEmpty() && customer!!.address.isNotEmpty()

            ProductButton(modifier = Modifier
                .fillMaxWidth()
                .padding((10.dp))
                .height(40.dp),
                text = "Ügyfél mentése",
                enabled = buttonEnabled,
                onClick = {
                    if (customer!!.phoneNumber.toDoubleOrNull() != null || customer!!.phoneNumber.toLongOrNull() != null) {
                        val customerToUpdate = hashMapOf(
                            "first_name" to customer?.firstName,
                            "last_name" to customer?.lastName,
                            "customer_address" to customer?.address,
                            "phone_number" to customer?.phoneNumber,
                            "customer_image" to customer?.image
                        ).toMap()
                        customerViewModel.updateCustomer(
                            customerToUpdate,
                            customerID!!,
                            onSuccess = {
                                navController.navigate(route = "CustomerScreen")
                                Toast.makeText(
                                    contextForToast,
                                    "Ügyfél módosítva",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailure = {
                                Toast.makeText(
                                    contextForToast,
                                    "Ügyfél nem lett módosítva",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                    } else {
                        Toast.makeText(
                            contextForToast,
                            "Csak számokat használj az telefonszám megadásánál",
                            Toast.LENGTH_LONG
                        ).show()
                    }
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