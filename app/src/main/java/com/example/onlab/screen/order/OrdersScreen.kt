package com.example.onlab.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.smallTopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlab.components.*
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Customer
import com.example.onlab.model.Order
import com.example.onlab.navigation.DestinationOrderList
import com.example.onlab.screen.customer.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onNavigate: (String, String) -> Unit,
    viewModel: OrdersListViewModel = hiltViewModel(),
    navigateFromTo: (String, String) -> Unit,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var kivalasztva = false
    val showDialog = remember { mutableStateOf(false) }


    showConfirmationDialog(
        showDialog = showDialog,
        message = "\"Biztos törölni szeretnéd a rendelést?\"",
        onConfirm = {
            viewModel.onDeleteOrder(selectedOrder?.id.toString()){}
        }) {
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                colors = smallTopAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary),
                title = { Text("Rendelések", color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary) },
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedItem = items[0]
            ){
                navigateFromTo(DestinationOrderList, it)
            }
        },
    ) { it ->
        it.calculateBottomPadding()
        Column(
            modifier = Modifier
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.surface)
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
            OrderItems(
                orderApiResponse = viewModel.ordersResponse,
                customerApiResponse = viewModel.customersResponse,
                onOrderItemClick = { order -> onNavigate(order.orderId.toString(), order.customerID) },
                onDeleteOrderItemClick = {
                    showDialog.value = true
                    selectedOrder = it
                 },
                onChangeOrderStatusClick = { viewModel.onUpdateOrder(it) }
            )
        }


    }
}

@Composable
fun OrderItems(
    orderApiResponse: ValueOrException<List<Order>>,
    customerApiResponse: ValueOrException<List<Customer>>,
    onOrderItemClick:(Order) -> Unit,
    onDeleteOrderItemClick:(Order) -> Unit,
    onChangeOrderStatusClick:(Order) -> Unit,
){
    when (orderApiResponse){
        is ValueOrException.Loading -> {
            LoadingScreen()
        }
        is ValueOrException.Failure -> Unit
        is ValueOrException.Success -> {
            CreateList(
                data = orderApiResponse.data.sortedBy { it.date },
                onClick = {
                    onOrderItemClick(it)
                },
                icons = listOf(
                    Icons.Default.Delete to {
                        onDeleteOrderItemClick(it)
                    },
                    Icons.Default.Check to {
                        onChangeOrderStatusClick(it)
                   },
                ),
                itemContent = { order ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                        ) {
                            when(customerApiResponse){
                                is ValueOrException.Loading -> LoadingScreen()
                                is ValueOrException.Failure -> Unit
                                is ValueOrException.Success -> {
                                    val customer = customerApiResponse.data.first{ customer ->
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
//                                    if (selectedOrder != null && kivalasztva){
//                                        kivalasztva = false
//                                    }
                                }
                            }
                        }
                    }

                }
            )
        }
    }
}
