@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.onlab.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val permissionRequester = PermissionRequester()

    val startDestination : String = if(FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
        DestinationLogin
    } else {
        DestinationCustomerList
    }

    NavHost(navController = navController, startDestination = startDestination){

        composable(DestinationProductList){
            ProductListScreen(
                onNavigate = {
                    Log.d("Nav", "productId: $it") // Log the value
                    navController.navigate(buildProductDetailsRoute(it))
                },
                orderItemViewModel = orderItemViewModel,
                navigateFromTo = { from, to ->
                    navController.navigate(to) {
                        popUpTo(from) { inclusive = true }
                    }
                },
                navigateBack = { navController.popBackStack()},
                ordering = false
            )
        }

        composable(route = DestinationNewProduct) {
            NewProductScreen(
                productViewModel = mProductViewModel,
                permissionRequester = permissionRequester,
                navigateBack = {
                    navController.popBackStack()
                }
            ){ from, to ->
                navController.navigate(to) {
                    popUpTo(from) { inclusive = true }
                }
            }
        }

        composable(route = DestinationProductDetails) {
            ProductDetailsScreen(
                navigateFromTo = { from, to ->
                    navController.navigate(to) {
                        popUpTo(from) { inclusive = true }
                    }
                },
                permissionRequester = permissionRequester,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(DestinationProductDetailsRoute){
            ProductDetailsScreen(
                navigateFromTo = { from , to ->
                    navController.navigate(to) {
                        popUpTo(from) {
                            inclusive = true
                        }
                    }
                },
                navigateBack = { navController.popBackStack() },
                permissionRequester = permissionRequester
            )
        }

        composable(DestinationCustomerList) {
            CustomerScreen(
                navController = navController,
                mCustomerViewModel = mCustomerViewModel,
                navigateBack = {
                    navController.popBackStack()
                }
            ){ from, to ->
                navController.navigate(to) {
                    popUpTo(from) {
                        inclusive = true
                    }
                }
            }
        }

        composable(DestinationNewCustomer){
            NewCustomerScreen(
                customerViewModel = mCustomerViewModel,
                permissionRequester = permissionRequester,
                navigateBack = { navController.popBackStack() }
            ){ from, to ->
                navController.navigate(to) {
                    popUpTo(from) {
                        inclusive = true
                    }
                }
            }
        }

        composable(DestinationOrderList){
            OrdersScreen(
                navController = navController,
                orderViewModel= orderViewModel,
                customerViewModel = customerViewModel,
                orderItemViewModel = orderItemViewModel,
                mProductViewModel = productViewModel,
                loginScreenViewModel = loginScreenViewModel,
                navigateBack = { navController.popBackStack()}
            ){ from, to ->
                navController.navigate(to) {
                    popUpTo(from) { inclusive = true }
                }
            }
        }

        composable(
            "$DestinationCustomerDetails/{customer}",
            arguments = listOf(
                navArgument(name = "customer") {
                    type = NavType.StringType
                }
            )
        ) {
            CustomerDetailsScreen(
                navigateFromTo = { from, to ->
                    navController.navigate(to) {
                        popUpTo(from) {
                            inclusive = true
                        }
                    }
                },
                navController = navController,
                customerViewModel = mCustomerViewModel,
                permissionRequester = permissionRequester
            )
        }


        composable("$DestinationNewOrder/{customer}/{orderId}",
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
            route = "${DestinationProductList}/{orderId}/{list}",
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.StringType
                },
                navArgument("list") {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val list = backStackEntry.arguments?.getBoolean("list") ?: false

            ProductListScreen(
                onNavigate = {
                    Log.d("Nav", "productId: $it")
                    navController.navigate(buildProductDetailsRoute(it))
                },
                orderID = orderId,
                ordering = list,
                orderItemViewModel = orderItemViewModel,
                navigateBack = {
                    navController.popBackStack()
                },
                navigateFromTo = { from, to ->
                    navController.navigate(to) {
                        popUpTo(from) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = "${DestinationProductList}/{list}",
            arguments = listOf(
                navArgument("list") {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            val list = backStackEntry.arguments?.getBoolean("list") ?: false

            ProductListScreen(
                onNavigate = {
                    Log.d("Nav", "productId: $it")
                    navController.navigate(buildProductDetailsRoute(it))
                },
                ordering = list,
                orderItemViewModel = orderItemViewModel,
                navigateBack = {
                    navController.popBackStack()
                },
                navigateFromTo = { from, to ->
                    navController.navigate(to) {
                        popUpTo(from) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = "${DestinationProductList}/{list}",
            arguments = listOf(
                navArgument("list") {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            val list = backStackEntry.arguments?.getBoolean("list") ?: false

            ProductListScreen(
                onNavigate = {
                    Log.d("Nav", "productId: $it")
                    navController.navigate(buildProductDetailsRoute(it))
                },
                ordering = list,
                orderItemViewModel = orderItemViewModel,
                navigateBack = {
                    navController.popBackStack()
                },
                navigateFromTo = { from, to ->
                    navController.navigate(to) {
                        popUpTo(from) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(DestinationLogin){
                LoginScreen(
                    navigateFromTo = { from , to ->
                        navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }},
                )
        }

        composable(DestinationProfile){
            ProfileScreen(navController = navController){ from, to ->
                navController.navigate(to) {
                    popUpTo(from) { inclusive = true }
                }
            }
        }
    }

}
fun buildProductDetailsRoute(argument: String) = "${DestinationProductDetails}/$argument"






