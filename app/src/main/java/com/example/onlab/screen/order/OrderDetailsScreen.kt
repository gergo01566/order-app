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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.*
import com.example.onlab.navigation.DestinationCustomerList
import com.example.onlab.navigation.DestinationNewOrder
import com.example.onlab.viewModels.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetailsScreen(
    state: List<MOrderItem>,
    orderViewModel: MOrderViewModel,
    onNavigateTo: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onUpdateOrderItem: (MOrderItem) -> Unit,
    onRemoveOrderItem: (MOrderItem) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    navigateFromTo: (String, String) -> Unit,
) {
    Log.d("update", "neworder: $state") // Log the value

//    LaunchedEffect(orderID) {
//        orderItemViewModel.initOrders(orderID!!)
//    }
//
//    orderItemViewModel.initOrders(orderID!!)
//    copyOfOrderItems = orderItemViewModel.getOrderItemsList()

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
            navigateFromTo(DestinationNewOrder, DestinationCustomerList)
        }
    }

    if (showEditDialog.value) {
        when(val productsResponse = viewModel.productsResponse){
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
                    Text(text = "Nem sikerült betölteni")
                }
            }
            is ValueOrException.Success -> {
                FullScreenDialog(
                    showDialog = showEditDialog,
                    selectedProduct = productsResponse.data.first{ it.id == selectedOrderItem!!.productID},
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
                                onUpdateOrderItem(updatedOrderItem)
                                //orderItemViewModel.updateOrderItem(updatedOrderItem)
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
    }



    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            selectedOrderItem?.let {
                onRemoveOrderItem(it)
                //orderItemViewModel.removeOrderItem(selectedOrderItem!!)
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
//            createTopBar(
//                navController = navController,
//                text = "${customer!!.firstName} rendelése",
//                withIcon = true,
//                onBack = {
//                    if(orderItemViewModel.isOrderChanged(orderID!!)){
//                        openDialog.value = true
//                    } else {
//                        orderItemViewModel.clearOrderItemsList()
//                        navController.popBackStack()
//                    }
//                }
//            )
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
                            onNavigateTo(viewModel.orderId.toString(), "true", viewModel.customerId.toString())
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
                        if (state.isNotEmpty()) {
                            openDialog.value = true
                        } else {
                            onNavigateBack()
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
                                if (state.isNotEmpty()) {
                                    openDialog.value = true
                                } else {
                                    navigateFromTo(DestinationNewOrder, DestinationCustomerList)
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

                                if (state.isEmpty()) {
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Rendelés mentése sikertelen, nincs rendelési tétel a listában."
                                        )
                                    }
                                }else if (orderViewModel.isOrderIncluded(viewModel.orderId.toString())) {
                                    state.forEach {
                                        viewModel.onSaveOrderItem(it)
                                    }
                                    //copyOfOrderItems.forEach { newVersion ->
//                                        val oldVersion = orderItemViewModel.getOrderItemsByOrder(orderId = viewModel.orderId.toString())
//                                            .find { it.id == newVersion.id }

//                                        if (oldVersion != null) {
//                                            val productToUpdate = hashMapOf(
//                                                "id" to newVersion.id,
//                                                "item_amount" to newVersion.amount,
//                                                "product_id" to newVersion.productID,
//                                                "is_karton" to newVersion.carton,
//                                                "order_id" to newVersion.orderID,
//                                                "is_piece" to newVersion.piece,
//                                                "status_id" to newVersion.statusID
//                                            ).toMap()
//                                            //orderItemViewModel.updateOrderItem(productToUpdate, oldVersion.id!!, {}, {})
//                                        } else {
//                                            //orderItemViewModel.saveOrderItemToFirebase(newVersion, {}, {})
//                                        }
                                    //}

                                } else {
//                                    copyOfOrderItems.forEach {
//                                        orderItemViewModel.saveOrderItemToFirebase(it, {
//                                            navigateFromTo(DestinationNewOrder, DestinationOrderList)
//                                            Toast.makeText(
//                                                currentContext,
//                                                "Rendelés hozzáadva",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        })
//                                    }

                                    //orderItemViewModel.clearOrderItemsList()
                                    viewModel.onSaveOrderToFirebae(
                                        MOrder(
                                            orderId = viewModel.orderId,
                                            date = LocalDate.now().toString(),
                                            customerID = viewModel.customerId.toString(),
                                            status = 0,
                                            madeby = FirebaseAuth.getInstance().currentUser!!.email!!
                                        )
                                    ) {
                                        Toast.makeText(
                                            currentContext,
                                            "Rendelés hozzáadva",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                onNavigateBack()
                                //orderItemViewModel.clearOrderItemsList()
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

            //copyOfOrderItems?.let {

            when (viewModel.orderItemsResponse){
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
                        Text(text = "Nem sikerült törölni a terméket")
                    }
                }
                is ValueOrException.Success -> {
                    CreateList(
                        data = state,
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
                            when(val productResponse = viewModel.productsResponse){
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
                                        Text(text = "Nem sikerült betölteni")
                                    }
                                }
                                is ValueOrException.Success -> {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(
                                            modifier = Modifier.size(70.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            AsyncImage(
                                                model = productResponse.data.first{ it.id == orderItem.productID}.image.toUri(),
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

                                            Text(
                                                text = productResponse.data.first{ it.id == orderItem.productID}.title,
                                                fontWeight = FontWeight.Bold
                                            )

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
                                }
                            }

                        })
                }
            }


            //}

        }
    }
}
