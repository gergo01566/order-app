package com.example.orderapp.screens.customers

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.orderapp.R
import com.example.orderapp.common.composables.BottomNavBar
import com.example.orderapp.common.composables.CreateList
import com.example.orderapp.common.composables.ShowConfirmationDialog
import com.example.orderapp.common.composables.items
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.Customer
import com.example.orderapp.navigation.DestinationCustomerDetails
import com.example.orderapp.navigation.DestinationCustomerList
import com.example.orderapp.screens.product_details.BasicField
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(
    viewModel: CustomerListViewModel = hiltViewModel(),
    onNavigateToCustomer: (String) -> Unit,
    onNavigateToOrder: (String, String) -> Unit,
    navigateInBottomBar: (String)->Unit,
    navigateFromTo:(String, String)->Unit
) {
    val contextForToast = LocalContext.current.applicationContext
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary),
                title = {
                    androidx.compose.material3.Text("Ügyfelek", color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                },
            )
        },
        bottomBar = {
            BottomNavBar(selectedItem = items[1]){
                navigateInBottomBar(it)
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
                            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.surface)
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
                            onClickOnOpenNewOrder = { customerId, orderId ->
                                onNavigateToOrder(customerId, orderId)
                            }
                        )
                    }
                }
            }

        }
    )

    ShowConfirmationDialog(
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
    androidx.compose.material3.ExtendedFloatingActionButton(
        modifier =  Modifier.padding(bottom = 60.dp),
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    ){
        Text(text = "Hozzáadás", color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
    }
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
    onClickOnOpenNewOrder: (String, String) -> Unit,
){
    CreateList(
        data = data.sortedBy { it.firstName },
        onClick = { customer ->
            val orderID: String = UUID.randomUUID().toString()
            customer.id?.let {
                    _customer -> onClickOnOpenNewOrder(_customer, orderID)
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
            modifier = Modifier.testTag("SearchBar"),
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