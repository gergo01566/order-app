package com.example.onlab.screen.order

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.onlab.model.MOrder
import com.example.onlab.model.Order
import com.example.onlab.model.OrderItem
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.*
import java.time.LocalDate
import java.util.*

@Composable
fun OrdersScreen(
    navController: NavController,
    orderViewModel: MOrderViewModel,
    customerViewModel: MCustomerViewModel,
    orderItemViewModel: MOrderItemViewModel,
    mProductViewModel: MProductViewModel
) {
    val contextForToast = LocalContext.current.applicationContext
    var selectedIndex by remember { mutableStateOf(0) }
    var orders = orderViewModel.getOrdersByStatus(selectedIndex)
    var selectedOrder by remember { mutableStateOf<MOrder?>(null) }
    var kivalasztva = false

    Scaffold(
        topBar = {
            createTopBar(
                navController = navController,
                text = "Rendelések",
                withIcon = false
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController as NavHostController,
                selectedItem = items[0]
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
            ToggleButtons(
                items = listOf("Függőben levő rendelések", "Teljesített rendelések"),
                selectedIndex = selectedIndex,
                onSelectedIndexChange = { index ->
                    selectedIndex = index
                    // do something else with the selected index
                }
            )

            CreateList(
                data = orders,
                onDelete = {
                    for (x in orderItemViewModel.getOrderItemsByOrder(it.id.toString())) {
                        orderItemViewModel.deleteOrderItem(
                            x.id.toString(),
                        ) {
                            Toast.makeText(
                                contextForToast,
                                "Rendelés törölve",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    orderViewModel.deleteOrder(it.id.toString(),{})
                },
                onEdit = {
                        kivalasztva = true
                         selectedOrder = it
                },
                onClick = {
                    var updatedOrder = it.copy()
                    if (it.status == 1) {
                        updatedOrder = it.copy(status = 0)
                        val OrderToUpdate = hashMapOf(
                            "order_status" to 0
                        ).toMap()
                        updatedOrder?.let { it1 -> orderViewModel.updateorder(OrderToUpdate, it.id.toString(),{
                            Toast.makeText(
                                contextForToast,
                                "Rendelés áthelyezve a függőben levő rendelésekhez",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }){} }
                    } else {
                        updatedOrder = it.copy(status = 1)
                        val OrderToUpdate = hashMapOf(
                            "order_status" to 1
                        ).toMap()
                        updatedOrder?.let { it1 -> orderViewModel.updateorder(OrderToUpdate, it.id.toString(),{
                            Toast.makeText(
                                contextForToast,
                                "Rendelés áthelyezve a teljesített rendelésekhez",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }){} }
                    }

                }, itemContent = { order ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                        ) {

                            customerViewModel.getCustomerById(order.customerID)
                                ?.let { it1 ->
                                    androidx.compose.material3.Text(
                                        text = it1.firstName + " " + it1.lastName,
                                        fontWeight = FontWeight.Bold
                                    )
                                    androidx.compose.material3.Text(
                                        text = order.date,
                                        fontWeight = FontWeight.Normal
                                    )
                                }

                        }

                        if (selectedOrder != null && kivalasztva){
                            orderViewModel.generatePDF(
                                contextForToast,
                                orderId = selectedOrder!!.orderId.toString(),
                                customerViewModel = customerViewModel,
                                mOrderItemViewModel = orderItemViewModel,
                                mProductViewModel = mProductViewModel
                            )
                            kivalasztva = false
                        }

                    }

                })
        }


    }
}