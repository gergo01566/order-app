package com.example.onlab.screen.order

import android.app.Activity
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.onlab.PermissionRequester
import com.example.onlab.components.*
import com.example.onlab.model.*
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import java.util.jar.Manifest

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewOrderScreen(
    navController: NavController,
    customerID: String? = null,
    orderID: String? = null,
    customerViewModel: MCustomerViewModel,
    orderItemViewModel: MOrderItemViewModel,
    productViewModel: MProductViewModel,
    orderViewModel: MOrderViewModel,
    permissionRequester: PermissionRequester
) {
    val customer by remember {
        mutableStateOf(customerViewModel.getCustomerById(customerID!!))
    }

    Log.d("DB", "New order screen , lista ennyi elemet tartalmaz: ${orderItemViewModel.getOrderItemsList().size}")

    var copyOfOrderItems by remember { mutableStateOf<List<MOrderItem>>(emptyList()) }

    LaunchedEffect(orderID) {
        orderItemViewModel.initOrders(orderID!!)
    }

    //orderItemViewModel.initOrders(orderID!!)
    copyOfOrderItems = orderItemViewModel.getOrderItemsList()

    val currentContext = LocalContext.current.applicationContext

    val showDialog = remember { mutableStateOf(false) }

    val showEditDialog = remember { mutableStateOf(false) }

    var selectedOrderItem by remember { mutableStateOf<MOrderItem?>(null) }

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        DismissChangesDialog(onDismiss = { openDialog.value = false }) {
            openDialog.value = false
            orderItemViewModel.clearOrderItemsList()
            navController.navigate("CustomerScreen")
        }
    }

    if (showEditDialog.value) {
        productViewModel.getProductById(selectedOrderItem?.productID.toString())?.let { product ->
            FullScreenDialog(
                showDialog = showEditDialog,
                selectedProduct = product,
                currentQuantity = selectedOrderItem?.amount,
                isKarton = selectedOrderItem?.carton,
                onAdd = { state: Boolean, quantity: String ->
                    selectedOrderItem?.let { orderItem ->
                        if (quantity.isDigitsOnly() && quantity.isNotEmpty()) {
                            val updatedOrderItem = MOrderItem(
                                id = orderItem.id, // Make sure to set the ID of the orderItem
                                amount = quantity.toInt(),
                                orderID = orderItem.orderID,
                                productID = orderItem.productID,
                                statusID = orderItem.statusID,
                                carton = !state,
                                piece = state
                            )
                            orderItemViewModel.updateOrderItem(updatedOrderItem)
                            Toast.makeText(
                                currentContext,
                                "Rendelési tétel módosítva",
                                Toast.LENGTH_SHORT
                            ).show()
                            showEditDialog.value = false
                        } else {
                            Toast.makeText(
                                currentContext,
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
                orderItemViewModel.removeOrderItem(selectedOrderItem!!)
//                orderItemViewModel.deleteOrderItem(it.id!!) {
//                    showDialog.value = false
//                    Toast.makeText(contextForToast, "Rendelési tétel törölve", Toast.LENGTH_SHORT)
//                        .show()
//                }
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
                    if(orderItemViewModel.isOrderChanged(orderID!!)){
                        openDialog.value = true
                    } else {
                        orderItemViewModel.clearOrderItemsList()
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
                        if (!copyOfOrderItems.isNullOrEmpty()) {
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
                                if (!copyOfOrderItems.isNullOrEmpty()) {
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
                                if (copyOfOrderItems.isNullOrEmpty()) {
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Rendelés mentése sikertelen, nincs rendelési tétel a listában."
                                        )
                                    }
                                }else if (orderViewModel.isOrderIncluded(orderID!!)) {
                                    copyOfOrderItems.forEach { newVersion ->
                                        val oldVersion = orderItemViewModel.getOrderItemsByOrder(orderId = orderID)
                                            .find { it.id == newVersion.id }

                                        if (oldVersion != null) {
                                            val productToUpdate = hashMapOf(
                                                "id" to newVersion.id,
                                                "item_amount" to newVersion.amount,
                                                "product_id" to newVersion.productID,
                                                "is_karton" to newVersion.carton,
                                                "order_id" to newVersion.orderID,
                                                "is_piece" to newVersion.piece,
                                                "status_id" to newVersion.statusID
                                            ).toMap()
                                            orderItemViewModel.updateOrderItem(productToUpdate, oldVersion.id!!, {}, {})
                                        } else {
                                            orderItemViewModel.saveOrderItemToFirebase(newVersion, {}, {})
                                        }
                                    }

                                } else {
                                    copyOfOrderItems.forEach {
                                        orderItemViewModel.saveOrderItemToFirebase(it, {
                                            navController.navigate("OrdersScreen")
                                            Toast.makeText(
                                                currentContext,
                                                "Rendelés hozzáadva",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                    }

                                    orderItemViewModel.clearOrderItemsList()
                                    orderViewModel.saveOrderToFirebase(
                                        MOrder(
                                            orderId = orderID,
                                            date = LocalDate.now().toString(),
                                            customerID = customerID.toString(),
                                            status = 0,
                                            madeby = FirebaseAuth.getInstance().currentUser!!.email!!
                                        ),
                                        {
                                            Toast.makeText(
                                                currentContext,
                                                "Rendelés hozzáadva",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }

                                navController.popBackStack()
                                orderItemViewModel.clearOrderItemsList()
//                                } else if(orderViewModel.isOrderIncluded(orderID!!)){
//                                    navController.popBackStack()
//                                    Log.d("TAG", "NewOrderScreen: updated")
//                                } else {
//                                    orderViewModel.saveOrderToFirebase(
//                                        MOrder(
//                                            orderId = orderID,
//                                            date = LocalDate.now().toString(),
//                                            customerID = customerID.toString(),
//                                            status = 0
//                                        ), {
//                                            navController.navigate("OrdersScreen")
//                                            Toast.makeText(
//                                                contextForToast,
//                                                "Rendelés hozzáadva",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        })
//                                }
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
            copyOfOrderItems?.let {
                CreateList(
                    data = copyOfOrderItems,
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
                            currentContext,
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
