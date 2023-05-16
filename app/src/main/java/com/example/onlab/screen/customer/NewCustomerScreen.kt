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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.components.BottomNavBar
import com.example.onlab.components.ImagePickerButton
import com.example.onlab.components.items
import com.example.onlab.model.Customer
import com.example.onlab.model.MCustomer
import com.example.onlab.screen.product.ProductButton
import com.example.onlab.viewModels.CustomerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.firestore.FirebaseFirestore

@ExperimentalPermissionsApi
@Composable
fun NewCustomerScreen(navController: NavController, customerViewModel: CustomerViewModel){
    val contextForToast = LocalContext.current.applicationContext

    var customer by remember { mutableStateOf(Customer(firstName = "", lastName = "", address = "", phoneNumber = "", image = "")) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )


    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }


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
                    Text(text = "Új ügyfél", fontSize = 27.sp, fontWeight = FontWeight.Normal)
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
                    value = customer.firstName,
                    onValueChange = { newValue ->
                        customer = customer.copy(firstName = newValue)
                    },
                    label = { Text(text = "Ügyfél keresztneve") },
                    placeholder = { Text(text = "Add meg az ügyfél nevét!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = customer.lastName,
                    onValueChange = { newValue ->
                        customer = customer.copy(lastName = newValue)
                    },
                    label = { Text(text = "Ügyfél vezetékneve") },
                    placeholder = { Text(text = "Add meg az ügyfél nevét!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = customer.address,//product.pricePerPiece.toString(),
                    onValueChange = { newValue ->
                        customer = customer.copy(address = newValue)
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
                        customer = customer.copy(phoneNumber = newValue)
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

                ProductButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp))
                    .height(40.dp),text = "Ügyfél mentése", onClick = {
                    if(customer.phoneNumber.toDoubleOrNull() != null || customer.phoneNumber.toLongOrNull() != null){
                        Toast.makeText(contextForToast, "Ügyfél hozzáadva", Toast.LENGTH_SHORT).show()
                        customerViewModel.addCustomer(customer = customer)
                        val mCustomer = MCustomer(firstName = customer.firstName, lastName = customer.lastName, address = customer.address, phoneNumber = customer.phoneNumber, image = customer.image)
                        saveToFirebase(navController = navController, customer = mCustomer)
                        //navController.navigate(route = "CustomerScreen")
                    } else {
                        Toast.makeText(contextForToast, "Kérlek használj megfelelő formátumot a telefonszám megadásánál!", Toast.LENGTH_SHORT).show()

                    }
                })
            }
        }
    )
}

fun saveToFirebase(customer: MCustomer, navController: NavController){
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("customers")

    if(customer.toString().isNotEmpty()){
        dbCollection.add(customer)
            .addOnSuccessListener{ documentRef->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener{task->
                        if(task.isSuccessful){
                            navController.popBackStack()
                        }
                    }
                    .addOnFailureListener{
                        Log.d("FB", "saveToFirebase: Error: $docId")
                    }
            }
    }
}