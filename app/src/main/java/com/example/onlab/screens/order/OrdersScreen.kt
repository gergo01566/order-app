package com.example.onlab.screens.order

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.onlab.components.*
import com.example.onlab.model.MOrder
import com.example.onlab.viewModels.*

@Composable
fun OrdersScreen(
    navController: NavController,
    orderViewModel: MOrderViewModel,
    customerViewModel: MCustomerViewModel,
    orderItemViewModel: MOrderItemViewModel,
    mProductViewModel: MProductViewModel,
    loginScreenViewModel: LoginScreenViewModel,
) {
    val contextForToast = LocalContext.current.applicationContext
    var selectedIndex by remember { mutableStateOf(0) }
    var orders = orderViewModel.getOrdersByStatus(selectedIndex)
    var selectedOrder by remember { mutableStateOf<MOrder?>(null) }
    var kivalasztva = false
    val loggedInUser = loginScreenViewModel.getCurrentUser()
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(loggedInUser) {
        if (loggedInUser != null) {
            // User is logged in, fetch the orders data
            orderViewModel.getAllOrdersFromDatabase() // Replace this with the actual function to fetch orders
            customerViewModel.getAllCustomersFromDatabase()
            mProductViewModel.getAllProductsFromDB()
            orderItemViewModel.getOrderItemsFromDatabase()
            orders = orderViewModel.getOrdersByStatus(1)
            orders = orderViewModel.getOrdersByStatus(0)
        }
    }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "\"Biztos törölni szeretnéd a rendelést?\"",
        onConfirm = {
            Log.d("TAG", "OrdersScreen: ${orderItemViewModel.getOrderItemsByOrder(selectedOrder?.id.toString())}")
            showDialog.value = false
            orderItemViewModel.getOrderItemsByOrder(selectedOrder?.orderId.toString()).forEach{
                orderItemViewModel.deleteOrderItem(it.id!!) {}
            }
            orderViewModel.deleteOrder(selectedOrder?.id.toString()) {
                Toast.makeText(
                    contextForToast,
                    "Rendelés törölve",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }) {

    }

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
                }
            )

            CreateList(
                data = orders.sortedBy { it.date },
                onDelete = {
                    selectedOrder = it
                    showDialog.value = true
                },
                onEdit = {
                    navController.navigate(route = "NewOrderScreen" + "/${customerViewModel.getCustomerById(it.customerID)!!.id}" + "/${it.orderId}")
                    Toast.makeText(
                        contextForToast,
                        "Ez a funkció nem érhető el",
                        Toast.LENGTH_SHORT,
                    ).show()
//                        kivalasztva = true
//                         selectedOrder = it
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

                }, iconContent = {
                                 CreateIcon(icons = Icons.Default.Info) {
                                     kivalasztva = true
                                     selectedOrder = it
                                 }
                }, itemContent = { order ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                        ) {

                            if (customerViewModel.getCustomerById(customerId = order.customerID) == null){
                                orderItemViewModel.getOrderItemsByOrder(orderId = order.orderId!!).forEach {
                                    orderItemViewModel.deleteOrderItem(it.id!!){}
                                }
                                order.id?.let { it1 -> orderViewModel.deleteOrder(it1){} }
                            }

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
