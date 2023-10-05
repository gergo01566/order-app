package com.example.onlab.screen.order

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.model.*
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
    val customer by remember {
        mutableStateOf(customerViewModel.getCustomerById(customerID!!))
    }

    val orderItems = orderID?.let { orderItemViewModel.getOrderItemsByOrder(it) }

    val contextForToast = LocalContext.current.applicationContext

    val showDialog = remember { mutableStateOf(false) }

    val showEditDialog = remember { mutableStateOf(false) }

    var selectedOrderItem by remember { mutableStateOf<MOrderItem?>(null) }

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val openDialog = remember { mutableStateOf(false) }

    if(openDialog.value){
        DismissChangesDialog(onDismiss = { openDialog.value = false }) {
            openDialog.value = false
            orderItems!!.forEach { mOrderItem ->
                orderItemViewModel.deleteOrderItem(mOrderItem.id!!) {}
            }
            navController.navigate("CustomerScreen")
        }
    }

    if (showEditDialog.value) {
        productViewModel.getProductById(selectedOrderItem?.productID.toString())?.let {
            FullScreenDialog(
                showDialog = showEditDialog,
                selectedProduct = it,
                currentQuantity = selectedOrderItem?.amount,
                isKarton = selectedOrderItem?.carton,
                onAdd = { state: Boolean, quantity: String ->
                    Log.d("TAG", "NewOrderScreen: ${selectedOrderItem?.id}")
                    selectedOrderItem?.let {
                        if (quantity.isDigitsOnly() && quantity!=""){
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
                                }) {
                                showEditDialog.value = false
                                Toast.makeText(
                                    contextForToast,
                                    "Rendelési tétel nem lett módosítva",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                contextForToast,
                                "A tétel mennyisége nem megfelelő formátumban van!",
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
            selectedOrderItem?.let {
                orderItemViewModel.deleteOrderItem(it.id!!) {
                    showDialog.value = false
                    Toast.makeText(contextForToast, "Rendelési tétel törölve", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            createTopBar(
                navController = navController,
                text = "${customer!!.firstName} rendelése",
                withIcon = true,
                onBack = {
                    if (!orderItems.isNullOrEmpty()) {
                        openDialog.value = true
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        },
        bottomBar = {
            androidx.compose.material3.BottomAppBar {
                Row(modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.material3.FloatingActionButton(

                        modifier = Modifier
                            .padding(end = 10.dp, start = 10.dp)
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = {
                            navController.navigate(
                                "${ProductScreens.ListScreen.name}/${
                                    UUID.fromString(
                                        orderID
                                    )
                                }/true"
                            )
                        },
                        containerColor = MaterialTheme.colors.primary,
                        elevation = androidx.compose.material3.FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Filled.Add,
                            "Add button",
                            tint = Color.White
                        )
                    }
                    BackHandler {
                        if (!orderItems.isNullOrEmpty()) {
                            openDialog.value = true
                        } else {
                            navController.popBackStack()
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        androidx.compose.material3.FloatingActionButton(
                            modifier = Modifier
                                .padding(end = 10.dp, start = 10.dp)
                                .weight(1f),
                            onClick = {
                                if (!orderItems.isNullOrEmpty()) {
                                    openDialog.value = true
                                } else {
                                    navController.navigate("CustomerScreen")
                                }

                            },
                            elevation = androidx.compose.material3.FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        ) {
                            Row(modifier = Modifier.padding(5.dp)) {
                                androidx.compose.material3.Text(text = "Mégsem")
                            }

                        }

                        androidx.compose.material3.FloatingActionButton(
                            modifier = Modifier
                                .padding(end = 10.dp, start = 10.dp)
                                .weight(1f),
                            onClick = {
                                if (orderItems.isNullOrEmpty()) {
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Rendelés mentése sikertelen, nincs rendelési tétel a listában."
                                        )
                                    }
                                } else {
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
                                }
                            },
                            elevation = androidx.compose.material3.FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        ) {
                            Row(modifier = Modifier.padding(5.dp)) {
                                androidx.compose.material3.Text(text = "Mentés")
                            }

                        }
                    }
                }
            }
        },
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = it.calculateBottomPadding())
        ) {
            orderItems?.let {
                CreateList(
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

        }
    }
}
