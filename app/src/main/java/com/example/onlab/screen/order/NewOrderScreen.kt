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
import com.example.onlab.model.*
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.*
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewOrderScreen(
    navController: NavController,
    customerID: String? = null,
    orderID: String? = null,
    customerViewModel: MCustomerViewModel,
    orderItemViewModel: MOrderItemViewModel,
    productViewModel: MProductViewModel,
    orderViewModel: MOrderViewModel
) {
    var customer by remember {
        mutableStateOf(customerViewModel.getCustomerById(customerID!!))
    }

    var orderItems = orderID?.let { orderItemViewModel.getOrderItemsByOrder(it) }

    val contextForToast = LocalContext.current.applicationContext

    val showDialog = remember { mutableStateOf(false) }

    val showEditDialog = remember { mutableStateOf(false) }

    var selectedOrderItem by remember { mutableStateOf<MOrderItem?>(null) }


    if (showEditDialog.value) {
        productViewModel.getProductById(selectedOrderItem?.productID.toString())?.let {
            FullScreenDialog(
                showDialog = showEditDialog,
                selectedProduct = it,
                currentQuantity = selectedOrderItem?.amount,
                isKarton = selectedOrderItem?.carton,
                onAdd = { state: Boolean, quantity: Int ->
                    Log.d("TAG", "NewOrderScreen: ${selectedOrderItem?.id}")
                    selectedOrderItem?.let {
                        //selectedOrderItem = it.copy(carton = !state, piece = state, amount = quantity)
                        val selectedOrderItemToUpdate = hashMapOf(
                            "item_amount" to quantity,
                            "is_karton" to !state,
                            "is_piece" to state,
                        ).toMap()
                        orderItemViewModel.updateOrderItem(selectedOrderItemToUpdate,
                            selectedOrderItem?.id.toString(),
                        {
                            Toast.makeText(
                                contextForToast,
                                "Rendelési tétel módosítva",
                                Toast.LENGTH_SHORT
                            ).show()
                            showEditDialog.value = false
                        }){
                            showEditDialog.value = false
                            Toast.makeText(
                                contextForToast,
                                "Rendelési tétel nem lett módosítva",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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
            selectedOrderItem?.let { orderItemViewModel.deleteOrderItem(it.id!!){
                showDialog.value = false
                Toast.makeText(contextForToast, "Rendelési tétel törölve", Toast.LENGTH_SHORT).show()
            } }
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
        floatingActionButtonPosition = FabPosition.End
    ) { it ->
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
                    CreateList<MOrderItem>(
                        data = orderItems,
                        onDelete = {
                            showDialog.value = true
                            selectedOrderItem = it
                        },
                        onEdit = {
                            Log.d("TAG", "edit it NewOrderScreen: ${it.id}")
                            selectedOrderItem = it
                            Log.d("TAG", "edit selected NewOrderScreen: ${selectedOrderItem?.id}")
                            showEditDialog.value = true
                            Toast.makeText(
                                contextForToast,
                                "${selectedOrderItem?.id}",
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
                                        model = productViewModel.getProductById(orderItem.productID)!!.image.toUri(),
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
                                    productViewModel.getProductById(orderItem.productID)
                                        ?.let { it1 ->
                                            Text(
                                                text = it1.title,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    if (orderItem.carton) {
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
                            orderViewModel.saveOrderToFirebase(
                                MOrder(
                                    orderId = orderID,
                                    date = LocalDate.now().toString(),
                                    customerID = customerID.toString(),
                                    status = 0
                                ), {
                                    navController.navigate("OrdersScreen")
                                    Toast.makeText(
                                        contextForToast,
                                        "Rendelés hozzáadva",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
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
}
