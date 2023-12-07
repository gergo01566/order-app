package com.example.orderapp.screens.order_details

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.orderapp.R
import com.example.orderapp.common.composables.*
import com.example.orderapp.common.snackbar.SnackbarManager
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.*
import com.example.orderapp.navigation.DestinationCustomerList
import com.example.orderapp.navigation.DestionationOrderDetails
import com.example.orderapp.screens.customers.LoadingScreen
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewOrderScreen(
    onNavigateTo: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    navigateFromTo: (String, String) -> Unit,
) {
    val state = viewModel.orderItemListState.collectAsStateWithLifecycle().value
    val dismissChangesDialog = remember { mutableStateOf(viewModel.changeMade) }

    OrderDetailsScreenContent(
        productResponse = viewModel.productsResponse,
        state = state,
        onSaveClick = {
            viewModel.onSaveClick()
            onNavigateBack()
        },
        onFloatingButtonClick = { onNavigateTo(viewModel.orderId.toString(), "true", viewModel.customerId.toString()) },
        onGeneratePDFClick = { viewModel.generatePDF() },
        onNavigateBack = {
            viewModel.onNavigateBack(
                { onNavigateBack() }
            ){
                dismissChangesDialog.value = true
            }
        },
        onDeleteOrderItem = {
            viewModel.updateOrderItemLocally(it)
        },
        onEditOrderItem = {
            viewModel.updateOrderItemLocally(it)
        },
    )
    DeleteOrder(apiResponse = viewModel.deleteOrderItemResponse)
    AddOrder(apiResponse = viewModel.saveOrderItemResponse)

    if (dismissChangesDialog.value) {
        DismissChangesDialog(onDismiss = { dismissChangesDialog.value = false }) {
            dismissChangesDialog.value = false
            navigateFromTo(DestionationOrderDetails, DestinationCustomerList)
        }
    }
}

@Composable
fun DeleteOrder(apiResponse: ValueOrException<Boolean>){
    when(apiResponse){
        is ValueOrException.Loading -> {
            LoadingScreen()
        }
        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreenContent(
    productResponse: ValueOrException<List<Product>>,
    state: ValueOrException<List<OrderItem>>,
    onSaveClick: () -> Unit,
    onGeneratePDFClick:()->Unit,
    onFloatingButtonClick:()->Unit,
    onNavigateBack:() -> Unit,
    onDeleteOrderItem: (OrderItem) -> Unit,
    onEditOrderItem: (OrderItem) -> Unit,
){
    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Rendelési tételek") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { onNavigateBack() }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.IconButton(onClick = {
                        onSaveClick()
                    }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Done,
                            contentDescription = "Save"
                        )
                    }
                    androidx.compose.material3.IconButton(onClick = {
                        onGeneratePDFClick()
                    }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Info,
                            contentDescription = "PDF"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                    onClick = {
                        onFloatingButtonClick()
                    },
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    elevation = androidx.compose.material3.FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.Add,
                        "Add button",
                        tint = Color.White
                    )
                }
        }
    ) { paddingValues ->
        OrderItemList(
            productResponse = productResponse,
            orderItemsResponse = state,
            onDeleteOrderItem = { onDeleteOrderItem(it) },
            onEditOrderItem = { onEditOrderItem(it) },
        )
        paddingValues.calculateBottomPadding()
    }
}

@Composable
fun OrderItemList(
    orderItemsResponse: ValueOrException<List<OrderItem>>,
    productResponse: ValueOrException<List<Product>>,
    onDeleteOrderItem: (OrderItem) -> Unit,
    onEditOrderItem: (OrderItem) -> Unit,
){
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    var selectedOrderItem by remember { mutableStateOf<OrderItem?>(null) }


    when(orderItemsResponse){
        is ValueOrException.Loading -> {
            LoadingScreen()
        }
        is ValueOrException.Failure -> Unit
        is ValueOrException.Success -> {
            CreateList(
                data = orderItemsResponse.data.filter { it.amount != 0 },
                icons = listOf(
                    Icons.Default.Delete to {
                        showDeleteDialog.value = true
                        selectedOrderItem = it
                    },
                    Icons.Default.Edit to {
                        showEditDialog.value = true
                        selectedOrderItem = it
                  },
                ),
                onClick = { }
            ) { orderItem ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.size(70.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ProductImage(
                            productResponse = productResponse,
                            orderItem = orderItem
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                    ) {
                        ProductName(productResponse = productResponse, orderItem = orderItem)
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
    }

    ShowConfirmationDialog(
        showDialog = showDeleteDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            selectedOrderItem?.let {
                val updatedOrderItem = OrderItem(
                    id = it.id, // Make sure to set the ID of the orderItem
                    amount = 0,
                    orderID = it.orderID,
                    productID = it.productID,
                    statusID = 0,
                    carton = it.carton,
                    piece = it.piece
                )
                onDeleteOrderItem(updatedOrderItem)
            }
        },
        onDismiss = {
            showDeleteDialog.value = false
        }
    )

    if (showEditDialog.value) {
        when(productResponse){
            is ValueOrException.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }
            is ValueOrException.Failure -> Unit
            is ValueOrException.Success -> {
                FullScreenDialog(
                    showDialog = showEditDialog,
                    selectedProduct = productResponse.data.first { it.id == selectedOrderItem!!.productID },
                    currentQuantity = selectedOrderItem?.amount,
                    isKarton = selectedOrderItem?.carton,
                    onAdd = { state: Boolean, quantity: String ->
                        selectedOrderItem?.let { orderItem ->
                            if (quantity.isDigitsOnly() && quantity.isNotEmpty()) {
                                val updatedOrderItem = OrderItem(
                                    id = orderItem.id, // Make sure to set the ID of the orderItem
                                    amount = quantity.toInt(),
                                    orderID = orderItem.orderID,
                                    productID = orderItem.productID,
                                    statusID = 0,
                                    carton = !state,
                                    piece = state
                                )
                                onEditOrderItem(updatedOrderItem)
                                showEditDialog.value = false
                            } else {
                                SnackbarManager.displayMessage(R.string.invalid_quantity)
                            }
                        }
                    }
                ) {
                    showEditDialog.value = false
                }
            }
        }
    }
}

@Composable
fun ProductImage(
    productResponse: ValueOrException<List<Product>>,
    orderItem: OrderItem
){
    when(productResponse){
        is ValueOrException.Success -> {
            AsyncImage(
                model = productResponse.data.first{ it.id == orderItem.productID}.image.toUri(),
                contentDescription = "profile image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentScale = ContentScale.Crop
            )
        }
        else -> LoadingScreen()
    }
}

@Composable
fun AddOrder(apiResponse: ValueOrException<Boolean>){
    when(apiResponse){
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> Log.d("log", "AddOrder: ${apiResponse.e}")
        else -> Unit
    }
}

@Composable
fun ProductName(
    productResponse: ValueOrException<List<Product>>,
    orderItem: OrderItem
){
    when(productResponse){
        is ValueOrException.Success -> {
            Text(
                text = productResponse.data.first{ it.id == orderItem.productID}.title,
                fontWeight = FontWeight.Bold
            )
        }
        else -> LoadingScreen()
    }
}
