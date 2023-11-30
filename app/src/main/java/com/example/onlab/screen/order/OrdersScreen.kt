package com.example.onlab.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlab.components.*
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Order
import com.example.onlab.navigation.DestinationOrderList
import com.example.onlab.screen.customer.LoadingScreen

@Composable
fun OrdersScreen(
    navigateBack: () -> Unit,
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
                    LoadingScreen()
                }
                is ValueOrException.Failure -> Unit
                is ValueOrException.Success -> {
                    CreateList(
                        data = ordersResponse.data.sortedBy { it.date },
                        onClick = {
                            onNavigate(it.orderId.toString(), it.customerID)
                        },
                        icons = listOf(
                            Icons.Default.Delete to {
                                selectedOrder = it
                                showDialog.value = true
                            },
                            Icons.Default.Check to { item -> viewModel.onUpdateOrder(item) },
                        ),
                        itemContent = { order ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(10.dp)
                                ) {
                                    when(val customerData = viewModel.customersResponse){
                                        is ValueOrException.Loading -> LoadingScreen()
                                        is ValueOrException.Failure -> Unit
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
