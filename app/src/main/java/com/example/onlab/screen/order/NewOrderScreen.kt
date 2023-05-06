package com.example.onlab.screen.order

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.model.OrderItem
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.CustomerViewModel
import com.example.onlab.viewModels.OrderItemViewModel
import java.util.*

@Composable
fun NewOrderScreen(
    navController: NavController,
    customerID: String? = null,
    orderID: String? = null,
    customerViewModel: CustomerViewModel,
    orderItemViewModel: OrderItemViewModel
) {
    var customer by remember {
        mutableStateOf(customerViewModel.getCustomerById(customerID!!))
    }

    var orderItems = orderID?.let { orderItemViewModel.getOrderItemsByOrder(it) }


    Scaffold(
        topBar = {
            createTopBar(navController = navController, text = "${customer!!.firstName} rendelése", withIcon = true)
        },
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController, selectedItem = items[1])
        },
        floatingActionButton = {
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
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    orderItems?.let { items ->
                        CreateList<OrderItem>(
                            data = orderItems,
                            onDelete = {
                                Log.d("TAG", "ProductListScreen: ${it.id}")
                                //showDialog.value = true
                                //selectedProduct = it
                            },
                            onEdit = {
                                navController.navigate(route = ProductScreens.NewProductScreen.name + "/${it.id}")
                            },
                            onClick = {
//                            if(list == true) {
//                                selectedProduct = it
//                                showFullScreenDialog.value = true
//                            }
//                            else{
//                                Log.d("TAG", "ProductListScreen: ez nem jott ossze")
//                            }
                            }, itemContent = { product ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.size(70.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
//                                        AsyncImage(
//                                            model = product.image.toUri(),
//                                            contentDescription = "profile image",
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .height(80.dp),
//                                            contentScale = ContentScale.Crop
//                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(10.dp)
                                    ) {
                                        Text(text = product.amount.toString(), fontWeight = FontWeight.Bold)
//                                        Text(
//                                            text = "${product.pricePerPiece}HUF / ${product.pricePerKarton}HUF",
//                                            style = MaterialTheme.typography.caption
//                                        )

                                    }
                                }
                            })
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp)
                    ) {
                        androidx.compose.material3.Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate(
                                    "${ProductScreens.ListScreen.name}/${
                                        UUID.fromString(
                                            orderID
                                        )
                                    }/true"
                                )
                            },
                            contentPadding = androidx.compose.material3.ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            androidx.compose.material3.Icon(
                                Icons.Filled.Add,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            androidx.compose.material3.Text("Hozzáadás")
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        androidx.compose.material3.Button(
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colors.secondary
                            ),
                            onClick = { },
                            contentPadding = androidx.compose.material3.ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            androidx.compose.material3.Icon(
                                Icons.Filled.Done,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            androidx.compose.material3.Text("Mentés")
                        }
                    }
                }
            }
        }
    )
}