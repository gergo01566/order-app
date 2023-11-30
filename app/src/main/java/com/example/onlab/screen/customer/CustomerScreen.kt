package com.example.onlab.screen.customer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.onlab.R
import com.example.onlab.components.*
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Customer
import com.example.onlab.navigation.DestinationCustomerDetails
import com.example.onlab.navigation.DestinationCustomerList
import com.example.onlab.screen.product.BasicField
import java.util.*

@Composable
fun CustomerScreen(
    viewModel: CustomerListViewModel = hiltViewModel(),
    onNavigateToCustomer: (String) -> Unit,
    onNavigateToOrder: (String, String) -> Unit,
    navigateBack: () -> Unit,
    navigateFromTo:(String, String)->Unit,
) {
    val contextForToast = LocalContext.current.applicationContext
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            createTopBar(text = "Ügyfelek", withIcon = false){
                navigateBack()
            }
        },
        bottomBar = {
            BottomNavBar(selectedItem = items[1]){
                navigateFromTo(DestinationCustomerList, it)
            }
        },
        floatingActionButton = {
            AddButton {
                navigateFromTo(DestinationCustomerList, DestinationCustomerDetails)
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = { it ->
            when (val customerResponse = viewModel.customerResponse){
                is ValueOrException.Loading -> {
                    LoadingScreen()
                }
                is ValueOrException.Failure -> Unit
                is ValueOrException.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = it.calculateBottomPadding())
                    ) {
                        SearchBar(
                            onSearchTextChanged = { newText ->
                                viewModel.onSearchTextChanged(newText)
                            }
                        )
                        CustomerList(
                            data = customerResponse.data,
                            onDelete = {
                                showDialog.value = true
                                selectedCustomer = it
                            },
                            onEditCustomerDetails = {
                                onNavigateToCustomer(it)
                            },
                            onClickOnOpenNewOrderIcon = { customerId, orderId ->
                                onNavigateToOrder(customerId, orderId)
                            }
                        )
                    }
                }
            }

        }
    )

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd az ügyfelet?",
        onConfirm = {
            viewModel.onDeleteCustomer(selectedCustomer?.id.toString()){
                Toast.makeText(contextForToast, "Ügyfél törölve", Toast.LENGTH_SHORT).show()
                showDialog.value = false
            }
        },
        onDismiss = {
            showDialog.value = false
        }
    )
}

@Composable
fun AddButton(onClick: () -> Unit){
    ExtendedFloatingActionButton(
        modifier =  Modifier.padding(bottom = 60.dp),
        text = { Text(text = "Hozzáadás") },
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(20.dp),
        backgroundColor = MaterialTheme.colors.primary,
    )
}

@Composable
fun LoadingScreen(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}

@Composable
fun CustomerList(
    data: List<Customer>,
    onDelete: (Customer)-> Unit,
    onEditCustomerDetails: (String)-> Unit,
    onClickOnOpenNewOrderIcon: (String, String) -> Unit,
){
    CreateList(
        data = data.sortedBy { it.firstName },
        onClick = { customer ->
            val orderID: String = UUID.randomUUID().toString()
            customer.id?.let {
                    _customer -> onClickOnOpenNewOrderIcon(_customer, orderID)
            }
        },
        icons = listOf(
            Icons.Default.Edit to { customer ->
                onEditCustomerDetails(customer.id.toString())
            },
            Icons.Default.Delete to { customer -> onDelete(customer) },
        ),
    ) { customer ->
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.size(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = customer.image.toUri(),
                    contentDescription = "profile image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = rememberImagePainter(
                        data = customer.image,
                        builder = {
                            placeholder(R.drawable.picture_placeholder)
                            error(R.drawable.picture_placeholder)
                        }) , contentDescription = "customer_image")
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ){
                Text(text = customer.firstName + " " + customer.lastName, fontWeight = FontWeight.Bold)
                Text(text = customer.address)
            }
        }
    }
}

@Composable
fun SearchBar(onSearchTextChanged: (String) -> Unit){
    var searchText by remember { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        BasicField(
            text = "Keresés",
            label = "Keresés",
            value = searchText,
            onNewValue = { newText ->
                searchText = newText
                onSearchTextChanged(newText)
            }
        )
    }
}