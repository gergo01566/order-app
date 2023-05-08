package com.example.onlab.screen.order

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.model.Customer
import com.example.onlab.model.Order
import com.example.onlab.model.OrderItem
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.CustomerViewModel
import com.example.onlab.viewModels.OrderItemViewModel
import com.example.onlab.viewModels.OrderViewModel
import com.example.onlab.viewModels.ProductViewModel
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewOrderScreen(
    navController: NavController,
    customerID: String? = null,
    orderID: String? = null,
    customerViewModel: CustomerViewModel,
    orderItemViewModel: OrderItemViewModel,
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel
) {
    var customer by remember {
        mutableStateOf(customerViewModel.getCustomerById(customerID!!))
    }

    var orderItems = orderID?.let { orderItemViewModel.getOrderItemsByOrder(it) }

    val contextForToast = LocalContext.current.applicationContext

    val showDialog = remember { mutableStateOf(false) }

    val showEditDialog = remember { mutableStateOf(false) }

    var selectedOrderItem by remember { mutableStateOf<OrderItem?>(null) }


    if (showEditDialog.value) {
        productViewModel.getProductById(selectedOrderItem?.productID.toString())?.let {
            FullScreenDialog(
                showDialog = showEditDialog,
                selectedProduct = it,
                currentQuantity = selectedOrderItem?.amount,
                isKarton = selectedOrderItem?.karton,
                onAdd = { state: Boolean, quantity: Int ->
                    selectedOrderItem?.let {
                        selectedOrderItem = it.copy(karton = !state, db = state, amount = quantity)
                        orderItemViewModel.updateOrderItem(selectedOrderItem!!)
                        Toast.makeText(
                            contextForToast,
                            "Rendelési tétel módosítva",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showEditDialog.value = false
                }
            ) {
                showEditDialog.value = false
            }
        }
    }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            selectedOrderItem?.let { orderItemViewModel.deleteOrderItem(it) }
            showDialog.value = false
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    Scaffold(
        topBar = {
            createTopBar(
                navController = navController,
                text = "${customer!!.firstName} rendelése",
                withIcon = true
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController as NavHostController,
                selectedItem = items[1]
            )
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
                                showDialog.value = true
                                selectedOrderItem = it
                            },
                            onEdit = {
                                selectedOrderItem = it
                                showEditDialog.value = true
                                Toast.makeText(
                                    contextForToast,
                                    "${selectedOrderItem?.amount}",
                                    Toast.LENGTH_SHORT
                                ).show()

                            },
                            onClick = {
//                            if(list == true) {
//                                selectedProduct = it
//                                showFullScreenDialog.value = true
//                            }
//                            else{
//                                Log.d("TAG", "ProductListScreen: ez nem jott ossze")
//                            }
                            }, itemContent = { orderItem ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.size(70.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        AsyncImage(
                                            model = productViewModel.getProductById(orderItem.productID.toString())!!.image.toUri(),
                                            contentDescription = "profile image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(80.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(10.dp)
                                    ) {
                                        productViewModel.getProductById(orderItem.productID.toString())
                                            ?.let { it1 ->
                                                Text(
                                                    text = it1.title,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        if (orderItem.karton) {
                                            Text(
                                                text = "${orderItem.amount} karton",
                                                style = MaterialTheme.typography.caption
                                            )
                                        } else {
                                            Text(
                                                text = "${orderItem.amount} darab",
                                                style = MaterialTheme.typography.caption
                                            )
                                        }
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
                            onClick = {
                                      orderViewModel.addOrder(Order(id = UUID.fromString(orderID), date = LocalDate.now(), customerID = UUID.fromString(customerID), status = 0))
                            },
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
