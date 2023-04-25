package com.example.onlab.screen.customer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.onlab.R
import com.example.onlab.components.BottomNavBar
import com.example.onlab.components.CreateList
import com.example.onlab.components.createTopBar
import com.example.onlab.model.Category
import com.example.onlab.model.Customer
import com.example.onlab.model.Product
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.screen.MenuBar
import com.example.onlab.screen.product.ProductViewModel

@Composable
fun CustomerScreen(navController: NavController, customerViewModel: CustomerViewModel) {

    val showDialog = remember { mutableStateOf(false) }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

    val customers = customerViewModel.customerList.collectAsState().value

    val searchText by customerViewModel.searchText.collectAsState()

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Column(modifier = Modifier.padding(5.dp)) {
                    Text("Biztos törölni szeretnéd a következő ügyfelet?")
                    Text(text = selectedCustomer!!.firstName + selectedCustomer!!.lastName)}
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedCustomer?.let { customerViewModel.removeCustomer(it) }
                        showDialog.value = false
                    }
                ) {
                    Text("Igen")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text("Nem")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            createTopBar(navController = navController, text = "Ügyfelek", withIcon = false)
        },
        bottomBar = {
            BottomNavBar(navController as NavHostController)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier =  Modifier.padding(bottom = 60.dp),
                text = { Text(text = "Új ügyfél") },
                onClick = {
                    //navController.navigate(route = ProductScreens.NewProductScreen.name)
                },
                shape = RoundedCornerShape(20.dp),
                backgroundColor = MaterialTheme.colors.primary,
            )
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
            ) {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = searchText,
                        onValueChange = customerViewModel::onSearchTextChange,
                        placeholder = { Text(text = "Keresés")})
                }

                CreateList(data = customers, {
                    showDialog.value = true
                    selectedCustomer = it
                }, {
                    //navController.navigate(route = ProductScreens.NewProductScreen.name+"/${it.id}")
                }
                ) { customer ->
                    Text(text = customer.firstName + " " + customer.lastName, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}