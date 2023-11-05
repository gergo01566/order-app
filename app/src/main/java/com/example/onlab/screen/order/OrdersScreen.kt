package com.example.onlab.screen.order

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.onlab.components.*
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MOrder
import com.example.onlab.navigation.DestinationOrderList
import com.example.onlab.viewModels.*

@Composable
fun OrdersScreen(
    navController: NavController,
    orderViewModel: MOrderViewModel,
    customerViewModel: MCustomerViewModel,
    orderItemViewModel: MOrderItemViewModel,
    mProductViewModel: MProductViewModel,
    navigateBack: () -> Unit,
    onNavigate: (String, String) -> Unit,
    viewModel: OrdersListViewModel = hiltViewModel(),
    navigateFromTo: (String, String) -> Unit,
) {
    val contextForToast = LocalContext.current.applicationContext
    var selectedIndex by remember { mutableStateOf(0) }
    var selectedOrder by remember { mutableStateOf<MOrder?>(null) }
    var kivalasztva = false
    val showDialog = remember { mutableStateOf(false) }


    showConfirmationDialog(
        showDialog = showDialog,
        message = "\"Biztos törölni szeretnéd a rendelést?\"",
        onConfirm = {
            Log.d("TAG", "OrdersScreen: ${orderItemViewModel.getOrderItemsByOrder(selectedOrder?.id.toString())}")
            showDialog.value = false
            orderItemViewModel.getOrderItemsByOrder(selectedOrder?.orderId.toString()).forEach{
                orderItemViewModel.deleteOrderItem(it.id!!) {}
            }
            viewModel.onDeleteOrder(selectedOrder?.id.toString()){
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
                text = "Rendelések",
                withIcon = false,
                onBack = {
                    navigateBack()
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedItem = items[0]
            ){
                navigateFromTo(DestinationOrderList, it)
            }
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
                    viewModel.switchToCompletedOrIncompleteOrders(index)
                    selectedIndex = index
                }
            )

            when (val ordersResponse = viewModel.ordersResponse){
                is ValueOrException.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
                is ValueOrException.Failure -> {
                    Snackbar {
                        androidx.compose.material.Text(text = "Nem sikerült törölni a terméket")
                    }
                }
                is ValueOrException.Success -> {
                    CreateList(
                        data = ordersResponse.data.sortedBy { it.date },
                        onDelete = {
                            selectedOrder = it
                            showDialog.value = true
                        },
                        onEdit = {
                            onNavigate(it.orderId.toString(), it.customerID)
                            //navController.navigate(route = "NewOrderScreen" + "/${customerViewModel.getCustomerById(it.customerID)!!.id}" + "/${it.orderId}")
                            Toast.makeText(
                                contextForToast,
                                "Ez a funkció nem érhető el",
                                Toast.LENGTH_SHORT,
                            ).show()
//                        kivalasztva = true
//                         selectedOrder = it
                        },
                        onClick = {
                            viewModel.onUpdateOrder(it)
//                            var updatedOrder = it.copy()
//                            if (it.status == 1) {
//                                updatedOrder = it.copy(status = 0)
//                                val OrderToUpdate = hashMapOf(
//                                    "order_status" to 0
//                                ).toMap()
//                                updatedOrder?.let { it1 -> orderViewModel.updateorder(OrderToUpdate, it.id.toString(),{
//                                    Toast.makeText(
//                                        contextForToast,
//                                        "Rendelés áthelyezve a függőben levő rendelésekhez",
//                                        Toast.LENGTH_SHORT,
//                                    ).show()
//                                }){} }
//                            } else {
//                                updatedOrder = it.copy(status = 1)
//                                val OrderToUpdate = hashMapOf(
//                                    "order_status" to 1
//                                ).toMap()
//                                updatedOrder?.let { it1 -> orderViewModel.updateorder(OrderToUpdate, it.id.toString(),{
//                                    Toast.makeText(
//                                        contextForToast,
//                                        "Rendelés áthelyezve a teljesített rendelésekhez",
//                                        Toast.LENGTH_SHORT,
//                                    ).show()
//                                }){} }
//                            }

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

//                                    if (customerViewModel.getCustomerById(customerId = order.customerID) == null){
//                                        orderItemViewModel.getOrderItemsByOrder(orderId = order.orderId!!).forEach {
//                                            orderItemViewModel.deleteOrderItem(it.id!!){}
//                                        }
//                                        order.id?.let { it1 -> orderViewModel.deleteOrder(it1){} }
//                                    }

                                    //viewModel.getCustomerById(order.customerID)

                                    when(val customerData = viewModel.customersResponse){
                                        is ValueOrException.Loading -> {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ){
                                                CircularProgressIndicator()
                                            }
                                        }
                                        is ValueOrException.Failure -> {
                                            Snackbar {
                                                androidx.compose.material.Text(text = "Nem sikerült betölteni a rendelést")
                                            }
                                        }
                                        is ValueOrException.Success -> {
                                            val customer = customerData.data.first{ customer ->
                                                customer.id == order.customerID
                                            }
                                            Text(
                                                text = customer.firstName + " " + customer.lastName,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = order.date,
                                                fontWeight = FontWeight.Normal
                                            )
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

                                    }

                                }



                            }


                        })
                }
            }


        }


    }
}
