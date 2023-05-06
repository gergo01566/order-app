package com.example.onlab.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.onlab.components.*

@Composable
fun NewOrderScreen(navController: NavController){
    Scaffold(
        topBar = {
            createTopBar(navController = navController, text = "Új rendelés", withIcon = false)
        },
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController, selectedItem = items[1])
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier =  Modifier.padding(bottom = 60.dp),
                text = { Text(text = "Új ügyfél") },
                onClick = {
                    navController.navigate(route = "NewCustomerScreen")
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
                    .padding(16.dp)) {
                    Text(text = "Alice felhasználó rendelése")
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(text = "Rendeléshez hozzáadott termékek")
                }
            }
        }
    )
}