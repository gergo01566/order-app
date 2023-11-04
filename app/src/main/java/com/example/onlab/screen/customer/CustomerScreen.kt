package com.example.onlab.screen.customer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
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
import com.example.onlab.model.MCustomer
import com.example.onlab.navigation.DestinationCustomerDetails
import com.example.onlab.navigation.DestinationCustomerList
import com.example.onlab.navigation.DestinationNewCustomer
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

    var selectedCustomer by remember { mutableStateOf<MCustomer?>(null) }

    var searchText by remember { mutableStateOf("") }

    val showDialog = remember { mutableStateOf(false) }

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
            ExtendedFloatingActionButton(
                modifier =  Modifier.padding(bottom = 60.dp),
                text = { Text(text = "Új ügyfél") },
                onClick = {
                    navigateFromTo(DestinationCustomerList, DestinationCustomerDetails)
                },
                shape = RoundedCornerShape(20.dp),
                backgroundColor = MaterialTheme.colors.primary,
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = { it ->
            when (val customerResponse = viewModel.customerResponse){
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
                    it.calculateBottomPadding()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = it.calculateBottomPadding())
                    ) {

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)) {

                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = searchText,
                                onValueChange = { newText ->
                                    viewModel.onSearchTextChanged(newText)
                                    searchText = newText
                                },
                                label = { Text("Keresés") }
                            )
                        }

                        CreateList(
                            data = customerResponse.data.sortedBy { it.firstName },
                            onDelete = {
                                showDialog.value = true
                                selectedCustomer = it
                            },
                            onEdit = {
                                onNavigateToCustomer(it.id.toString())
                            },
                            iconContent = {
                                CreateIcon(Icons.Rounded.ShoppingCart){
                                    val orderID: String = UUID.randomUUID().toString()
                                    it.id?.let { it1 -> onNavigateToOrder(it1, orderID) }
                                }
                            }
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
                }
            }

        }
    )
}