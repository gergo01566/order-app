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
import java.time.LocalDate
import java.util.*

@Composable
fun OrdersScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    customerViewModel: CustomerViewModel,
    orderItemViewModel: OrderItemViewModel
) {
    var selectedIndex by remember { mutableStateOf(0) }
    var orders = orderViewModel.getOrdersByStatus(selectedIndex)

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
        floatingActionButtonPosition = FabPosition.End,
        content = { it ->
            it.calculateBottomPadding()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = it.calculateBottomPadding())
            ){
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
                        for (x in orderItemViewModel.getOrderItemsByOrder(it.id.toString())){
                            orderItemViewModel.deleteOrderItem(x);
                        }
                        orderViewModel.deleteOrder(it)
                    },
                    onEdit = {
                    },
                    onClick = {
                        var updatedOrder = it.copy()
                        if(it.status == 1){
                            updatedOrder = it.copy(status = 0)
                            updatedOrder?.let { it1 -> orderViewModel.updateOrder(it1) }
                        } else {
                            updatedOrder = it.copy(status = 1)
                            updatedOrder?.let { it1 -> orderViewModel.updateOrder(it1) }
                        }

                    }, itemContent = { order ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(10.dp)
                            ) {

                                customerViewModel.getCustomerById(order.customerID.toString())
                                    ?.let { it1 ->
                                        androidx.compose.material3.Text(text = it1.firstName + " " + it1.lastName, fontWeight = FontWeight.Bold)
                                        androidx.compose.material3.Text(text = order.date.toString(), fontWeight = FontWeight.Normal)
                                    }

                            }
                        }

                    })
            }



        }
    )
}