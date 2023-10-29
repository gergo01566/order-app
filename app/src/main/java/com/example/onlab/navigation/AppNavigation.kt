@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.onlab.navigation

import AppState
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.onlab.PermissionRequester

import com.example.onlab.screen.ProductListScreen
import com.example.onlab.screen.customer.CustomerDetailsScreen
import com.example.onlab.screen.customer.CustomerScreen
import com.example.onlab.screen.customer.NewCustomerScreen
import com.example.onlab.screen.order.NewOrderScreen
import com.example.onlab.screen.order.OrdersScreen
import com.example.onlab.screen.product.NewProductScreen
import com.example.onlab.screen.product.ProductDetailsScreen
import com.example.onlab.screen.profile.ProfileScreen
import com.example.onlab.screens.login.LoginScreen
import com.example.onlab.viewModels.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val productViewModel = viewModel<MProductViewModel>()
    val customerViewModel = viewModel<MCustomerViewModel>()
    val orderItemViewModel = viewModel<MOrderItemViewModel>()
    val orderViewModel = viewModel<MOrderViewModel>()
    val mCustomerViewModel = viewModel<MCustomerViewModel>()
    val mProductViewModel = viewModel<MProductViewModel>()
    val loginScreenViewModel = viewModel<LoginScreenViewModel>()
    val loginViewModel = viewModel<LoginViewModel>()
    val permissionRequester = PermissionRequester()

    val startDestination : String = if(FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
        "LoginScreen";
    } else {
        "CustomerScreen";
    }

    NavHost(navController = navController, startDestination = startDestination){
        composable(DestinationProductList){
            ProductListScreen(onNavigate = {
                Log.d("Nav", "productId: $it") // Log the value
                navController.navigate(buildDetailsRoute(it))
            },navController = navController,ordering = false, productViewModel = mProductViewModel, customerViewModel = mCustomerViewModel, orderItemViewModel = orderItemViewModel)
        }

        composable(route = ProductScreens.NewProductScreen.name)
        {
            NewProductScreen(navController = navController, productViewModel = mProductViewModel, permissionRequester = permissionRequester)
        }
//        composable(
//            DestinationProductDetails + "/{product}",
//        arguments = listOf(navArgument(name = "product"){type = NavType.StringType})
//        ){ navBackStackEntry ->
//            ProductDetailsScreen(navController = navController, navBackStackEntry.arguments?.getString("product"), productViewModel = mProductViewModel, permissionRequester = permissionRequester)
//        }

        composable(DestinationProductDetailsRoute){
            ProductDetailsScreen(navigateFromTo = { from , to ->
                navController.navigate(to) {
                    popUpTo(from) { inclusive = true }
                }},navController = navController ,it.arguments?.getString("product"), productViewModel = mProductViewModel, permissionRequester = permissionRequester)
        }

//        composable(route = ProductScreens.DetailsScreen.name){
//            ProductDetailsScreen(onNavigate = {
//                                              navController.navigate(buildDetailsRoute(it))
//            },navController = navController, it.arguments?.getString("product"), productViewModel = mProductViewModel, permissionRequester = permissionRequester)
//        }

        //composable()

        composable("CustomerScreen") { // add CustomerScreen composable
            CustomerScreen(navController = navController, mCustomerViewModel = mCustomerViewModel)
        }
        composable("NewCustomerScreen"){
            NewCustomerScreen(navController = navController, customerViewModel = mCustomerViewModel, permissionRequester = permissionRequester)
        }
        composable("OrdersScreen"){
            OrdersScreen(navController = navController,orderViewModel= orderViewModel, customerViewModel = customerViewModel, orderItemViewModel = orderItemViewModel, mProductViewModel = productViewModel, loginScreenViewModel = loginScreenViewModel)
        }
        composable("CustomerDetailsScreen" + "/{customer}",
            arguments = listOf(navArgument(name = "customer"){
                type = NavType.StringType
            })){ navBackStackEntry ->
            CustomerDetailsScreen(navController = navController, navBackStackEntry.arguments?.getString("customer") ,customerViewModel = mCustomerViewModel, permissionRequester = permissionRequester)
        }
        composable("NewOrderScreen/{customer}/{orderId}",
            arguments = listOf(
                navArgument(name = "customer") { type = NavType.StringType },
                navArgument(name = "orderId") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            NewOrderScreen(
                navController = navController,
                customerID = backStackEntry.arguments?.getString("customer"),
                orderID = orderId,
                customerViewModel = customerViewModel,
                orderItemViewModel = orderItemViewModel,
                productViewModel = productViewModel,
                orderViewModel = orderViewModel,
                permissionRequester = permissionRequester
            )
        }
        composable(
            route = "${DestinationProductList}/{orderId}/{list}", // include customerId parameter in the route
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }, // add argument for customerId
                navArgument("list") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val list = backStackEntry.arguments?.getBoolean("list") ?: false
            ProductListScreen(onNavigate = {
                Log.d("Nav", "productId: $it") // Log the value
                navController.navigate(buildDetailsRoute(it))
            }, navController = navController, orderID = orderId, ordering = list, productViewModel = mProductViewModel, customerViewModel = mCustomerViewModel, orderItemViewModel = orderItemViewModel)
        }
        composable(
            route = "${DestinationProductList}/{list}", // include customerId parameter in the route
            arguments = listOf(
                navArgument("list") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val list = backStackEntry.arguments?.getBoolean("list") ?: false
            ProductListScreen(onNavigate = {
                Log.d("Nav", "productId: $it") // Log the value
                navController.navigate(buildDetailsRoute(it))
            }, navController = navController, ordering = list, productViewModel = mProductViewModel, customerViewModel = mCustomerViewModel, orderItemViewModel = orderItemViewModel)
        }
        composable("LoginScreen"){
                LoginScreen(
                    navigateFromTo = { from , to ->
                        navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }},
                    viewModel = loginViewModel)
        }
        composable("ProfileScreen"){
            ProfileScreen(navController = navController)
        }
    }

    fun loadAllDatas(){
        productViewModel.getAllProductsFromDB()
        customerViewModel.getAllCustomersFromDatabase()
        orderViewModel.getOrdersByStatus(0)
        orderViewModel.getOrdersByStatus(1)
    }
}
fun buildDetailsRoute(argument: String) = "${DestinationProductDetails}/$argument"

const val DestinationProductList = "ListScreen"
const val DestinationProductDetails = "ProductDetailsScreen"
const val DestinationOneArg = "arg"
const val DestinationProductDetailsRoute = "$DestinationProductDetails/{$DestinationOneArg}"






