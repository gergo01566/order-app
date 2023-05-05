package com.example.onlab.screen.customer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.R
import com.example.onlab.components.*
import com.example.onlab.model.Category
import com.example.onlab.model.Customer
import com.example.onlab.model.Product
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.screen.MenuBar
import com.example.onlab.screen.product.ProductViewModel

@Composable
fun CustomerScreen(navController: NavController, customerViewModel: CustomerViewModel) {

    val showDialog = remember { mutableStateOf(false) }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

    val customers = customerViewModel.customerList.collectAsState().value

    val searchText by customerViewModel.searchText.collectAsState()

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            selectedCustomer?.let { customerViewModel.removeCustomer(it) }
            showDialog.value = false
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
                        value = searchText,
                        onValueChange = customerViewModel::onSearchTextChange,
                        placeholder = { Text(text = "Keresés")})
                }

                CreateList(data = customers, {
                    showDialog.value = true
                    selectedCustomer = it
                }, {
                    navController.navigate(route = "CustomerDetailsScreen" + "/${it.id}")
                },{
                    CreateIcon(Icons.Rounded.ShoppingCart){
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