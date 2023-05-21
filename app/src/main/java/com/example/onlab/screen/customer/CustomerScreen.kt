package com.example.onlab.screen.customer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.onlab.R
import com.example.onlab.components.*
import com.example.onlab.data.DataOrException
import com.example.onlab.model.Customer
import com.example.onlab.model.MCustomer
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.CustomerViewModel
import com.example.onlab.viewModels.MCustomerViewModel
import java.util.*

@Composable
fun CustomerScreen(navController: NavController, mCustomerViewModel: MCustomerViewModel) {


    var selectedCustomer by remember { mutableStateOf<MCustomer?>(null) }

    var listOfCustomers = emptyList<MCustomer>()

    if (!mCustomerViewModel.data.value.data.isNullOrEmpty()){
        Log.d("FBB", "CustomerScreen: ${mCustomerViewModel.data.value.data!!.toList().toString()}")
        listOfCustomers = mCustomerViewModel.data.value.data!!.toList()
    }

    val showDialog = remember { mutableStateOf(false) }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            mCustomerViewModel.deleteCustomer(selectedCustomer?.id.toString()){
                showDialog.value = false
                mCustomerViewModel.getAllCustomersFromDatabase()
            }
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    Scaffold(
        topBar = {
            createTopBar(navController = navController, text = "Ügyfelek", withIcon = false)
        },
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController, selectedItem = items[1])
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier =  Modifier.padding(bottom = 60.dp),
                text = { Text(text = "Új ügyfél") },
                onClick = {
                    navController.navigate(route = "NewCustomerScreen")
                },
                shape = RoundedCornerShape(20.dp),
                backgroundColor = MaterialTheme.colors.primary,
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = { it ->
            if(mCustomerViewModel.data.value.loading == true){
                LinearProgressIndicator(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp))
            }
            it.calculateBottomPadding()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = it.calculateBottomPadding())
            ) {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = mCustomerViewModel.searchText.value,
                        onValueChange = { newText ->
                            mCustomerViewModel.onSearchTextChanged(newText)
                        },
                        label = { Text("Keresés") }
                    )
                }

                CreateList(
                    data = listOfCustomers,
                    onDelete = {
                    showDialog.value = true
                    selectedCustomer = it
                    },
                    onEdit = {
                    navController.navigate(route = "CustomerDetailsScreen" + "/${it.id.toString()}")
                    },
                    iconContent = {
                    CreateIcon(Icons.Rounded.ShoppingCart){
                        val orderID: String = UUID.randomUUID().toString()
                        navController.navigate(route = "NewOrderScreen" + "/${it.id}" + "/${orderID}")
                    }
                    }
                ) { customer ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.size(70.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = customer.image.toUri(),
                                contentDescription = "profile image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                contentScale = ContentScale.Crop
                            )
                            Image(
                                painter = rememberImagePainter(
                                    data = customer.image,
                                    builder = {
                                        placeholder(R.drawable.picture_placeholder)
                                        error(R.drawable.picture_placeholder)
                                    }) , contentDescription = "customer_image")
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ){
                            Text(text = customer.firstName + " " + customer.lastName, fontWeight = FontWeight.Bold)
                            }
                        }
                }
            }
        }
    )
}