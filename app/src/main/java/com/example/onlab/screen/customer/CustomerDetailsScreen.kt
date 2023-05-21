package com.example.onlab.screen.customer

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import com.example.onlab.components.BottomNavBar
import com.example.onlab.components.ImagePickerButton
import com.example.onlab.components.items
import com.example.onlab.components.showConfirmationDialog
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

    //TODO: megnezni
    var customer by remember {
        mutableStateOf(customerViewModel.data.value.data?.first{ mCustomer ->
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

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            customerViewModel.deleteCustomer(customerID){
                showDialog.value = false
                navController.navigate(route = "CustomerScreen")
                customerViewModel.getAllCustomersFromDatabase()
            }
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                modifier = Modifier.height(70.dp)
            ) {
                Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow Back",
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        })
                    Spacer(modifier = Modifier.padding(10.dp))
                    if(customerList.loading == true){
                        LinearProgressIndicator()
                        customerList.loading = false
                    } else {
                        Text(text = customer!!.firstName + " " + customer!!.lastName + " adatai", fontSize = 27.sp, fontWeight = FontWeight.Normal)
                    }
                }
            }
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
                    value = customer?.firstName ?: "",
                    onValueChange = { newValue ->
                        customer = customer?.copy(firstName = newValue) ?: customer
                    },
                    label = { Text(text = "Ügyfél keresztneve") },
                    placeholder = { Text(text = "Add meg az ügyfél nevét!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = customer?.lastName ?: "",
                    onValueChange = { newValue ->
                        customer = customer?.copy(lastName = newValue) ?: customer
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
                        customer = customer?.copy(address = newValue) ?: customer
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
                        customer = customer?.copy(phoneNumber = newValue) ?: customer
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

                ProductButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp))
                    .height(40.dp),
                    text = "Ügyfél mentése",
                    onClick = {
                        if(customer!!.phoneNumber.toDoubleOrNull() != null || customer!!.phoneNumber.toLongOrNull() != null){
                            val customerToUpdate = hashMapOf(
                                "first_name" to customer?.firstName,
                                "last_name" to customer?.lastName,
                                "customer_address" to customer?.address,
                                "phone_number" to customer?.phoneNumber,
                                "customer_image" to customer?.image
                            ).toMap()
                            customerViewModel.updateCustomer(customerToUpdate, customerID!!, onSuccess = {
                                navController.navigate(route = "CustomerScreen")
                                Toast.makeText(contextForToast, "Termék módosítva", Toast.LENGTH_SHORT).show()
                                customerViewModel.getAllCustomersFromDatabase()
                            }, onFailure = {
                                Toast.makeText(contextForToast, "Termék nem lett módosítva", Toast.LENGTH_SHORT).show()
                            })
                        }else {
                            Toast.makeText(contextForToast, "Csak számokat használj az ár megadásánál", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                ProductButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp))
                    .height(40.dp),
                    text = "Ügyfél törlése",
                    onClick = {
                        showDialog.value = true
                    },
                    color = Color.Red)
            }
        }
    )
}