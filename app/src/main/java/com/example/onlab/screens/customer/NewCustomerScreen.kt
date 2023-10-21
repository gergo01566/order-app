package com.example.onlab.screens.customer

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.PermissionRequester
import com.example.onlab.components.*
import com.example.onlab.model.MCustomer
import com.example.onlab.screens.product.ProductButton
import com.example.onlab.viewModels.MCustomerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@Composable
fun NewCustomerScreen(navController: NavController, customerViewModel: MCustomerViewModel, permissionRequester: PermissionRequester){
    val contextForToast = LocalContext.current.applicationContext

    var customer by remember { mutableStateOf(MCustomer(firstName = "", lastName = "", address = "", phoneNumber = "", image = "")) }

    var buttonEnabled by remember { mutableStateOf(false) }

    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    Scaffold(
        topBar = {
            createTopBar(
                navController = navController,
                text = "Új ügyfél",
                withIcon = true,
                onBack = {
                    if (buttonEnabled){
                        showAlertDialog = true
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController, selectedItem = items[1])
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = { padding ->
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
                    value = customer.firstName,
                    onValueChange = { newValue ->
                        if(newValue.length <= 20) customer = customer.copy(firstName = newValue)
                        else Toast.makeText(contextForToast, "A keresztnév max. 20 karakter lehet", Toast.LENGTH_SHORT).show()
                    },
                    maxLines = 1,
                    label = { Text(text = "Ügyfél keresztneve") },
                    placeholder = { Text(text = "Add meg az ügyfél nevét!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = customer.lastName,
                    onValueChange = { newValue ->
                        if(newValue.length <= 20) customer = customer.copy(lastName = newValue)
                        else Toast.makeText(contextForToast, "A vezetéknév max. 20 karakter lehet", Toast.LENGTH_SHORT).show()
                    },
                    label = { Text(text = "Ügyfél vezetékneve") },
                    placeholder = { Text(text = "Add meg az ügyfél nevét!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = customer.address,
                    onValueChange = { newValue ->
                        if(newValue.length <= 30) customer = customer.copy(address = newValue)
                        else Toast.makeText(contextForToast, "A cím max. 30 karakter lehet", Toast.LENGTH_SHORT).show()
                    },
                    label = { Text(text = "Ügyfél címe") },
                    placeholder = { Text(text = "Add meg az ügyfél címét!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = customer.phoneNumber,
                    onValueChange = { newValue ->
                        if(newValue.length <= 15) customer = customer.copy(phoneNumber = newValue)
                        else Toast.makeText(contextForToast, "A telefonszám max. 15 számjegyű lehet", Toast.LENGTH_SHORT).show()
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
                        imageUri = it
                        customer = customer.copy(image = it.toString())
                    }, permissionRequester = permissionRequester)
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "profile image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                BackHandler {
                    if(buttonEnabled){
                        showAlertDialog = true
                    } else {
                        navController.popBackStack()
                    }
                }

                if (showAlertDialog){
                    DismissChangesDialog(
                        onDismiss = { showAlertDialog = false },
                        onConfirm = {
                            navController.popBackStack()
                            showAlertDialog = false
                        }
                    )
                }

                buttonEnabled = customer.firstName.isNotEmpty() && customer.lastName.isNotEmpty() && customer.address.isNotEmpty()

                ProductButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp))
                        .height(40.dp),
                    text = "Ügyfél mentése",
                    enabled =  buttonEnabled,
                    onClick = {
                    if(customer.phoneNumber.toDoubleOrNull() != null || customer.phoneNumber.toLongOrNull() != null || customer.firstName.isNotEmpty()){
                        val mCustomer = MCustomer(firstName = customer.firstName, lastName = customer.lastName, address = customer.address, phoneNumber = customer.phoneNumber, image = imageUri.toString())
                        customerViewModel.saveCustomerToFirebase(mCustomer, onSuccess = {
                            Toast.makeText(contextForToast, "Ügyfél hozzáadva", Toast.LENGTH_SHORT).show()
                            navController.navigate(route = "CustomerScreen")
                            customerViewModel.getAllCustomersFromDatabase()
                        }, {
                            Log.d("FB", "saveToFirebase: Error:")
                        })
                    } else {
                        Toast.makeText(contextForToast, "Kérlek használj megfelelő formátumot a telefonszám megadásánál!", Toast.LENGTH_SHORT).show()

                    }
                })
            }
        }
    )
}