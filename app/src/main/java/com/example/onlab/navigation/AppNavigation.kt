@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.onlab.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.onlab.screen.ProductListScreen
import com.example.onlab.screen.customer.CustomerDetailsScreen
import com.example.onlab.screen.customer.CustomerScreen
import com.example.onlab.screen.customer.CustomerViewModel
import com.example.onlab.screen.customer.NewCustomerScreen
import com.example.onlab.screen.product.NewProductScreen
import com.example.onlab.screen.product.ProductDetailsScreen
import com.example.onlab.screen.product.ProductViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val productViewModel = viewModel<ProductViewModel>()
    val customerViewModel = viewModel<CustomerViewModel>()
    NavHost(navController = navController, startDestination = ProductScreens.ListScreen.name){
        composable(ProductScreens.ListScreen.name){
            ProductListScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(ProductScreens.NewProductScreen.name)
        {
            NewProductScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(ProductScreens.NewProductScreen.name + "/{product}",
        arguments = listOf(navArgument(name = "product"){type = NavType.StringType})
        ){ navBackStackEntry ->  
            ProductDetailsScreen(navController = navController, navBackStackEntry.arguments?.getString("product"), productViewModel = productViewModel)
        }
        composable("CustomerScreen") { // add CustomerScreen composable
            CustomerScreen(navController = navController, customerViewModel = customerViewModel)
        }
        composable("NewCustomerScreen"){
            NewCustomerScreen(navController = navController, customerViewModel = customerViewModel)
        }
        composable("CustomerDetailsScreen" + "/{customer}",
            arguments = listOf(navArgument(name = "customer"){
                type = NavType.StringType
            })){ navBackStackEntry ->
            CustomerDetailsScreen(navController = navController, navBackStackEntry.arguments?.getString("customer") ,customerViewModel = customerViewModel)
        }
    }
}