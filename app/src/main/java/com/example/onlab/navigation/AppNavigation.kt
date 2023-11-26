@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.onlab.navigation

import EditProfileScreen
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlab.PermissionRequester
import com.example.onlab.screen.customer.CustomerDetailsScreen

import com.example.onlab.screen.product.ProductListScreen
import com.example.onlab.screen.customer.CustomerScreen
import com.example.onlab.screen.order.NewOrderScreen
import com.example.onlab.screen.order.OrdersScreen
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
fun AppNavigation() {
    val navController = rememberNavController()
    val permissionRequester = PermissionRequester()

    val startDestination : String = if(FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
        DestinationLogin
    } else {
        DestinationCustomerList
    }

    NavHost(
        navController = navController,
        startDestination = "orderAppRoute"
    ) {
        navigation(
            startDestination = startDestination,
            route = "orderAppRoute"
        ) {

            composable(DestinationLogin){
                LoginScreen(
                    navigateFromTo = { from , to ->
                        navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }},
                )
            }

            composable(DestinationEditProfile){
                logBackStack(it, navController)
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    navigateFromTo = { from , to ->
                        navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }},
                )
            }

            composable(DestinationOrderList){
                OrdersScreen(
                    onNavigate = { orderId, customerId ->
                        navController.navigate(buildNewOrderRoute(customerId = customerId, orderId = orderId))
                    },
                    navigateBack = { navController.popBackStack()}
                ){ from, to ->
                    navController.navigate(to) {
                        popUpTo(from) { inclusive = true }
                    }
                }
            }

            composable(DestinationOrderDetailsRoute) {
                logBackStack(it, navController)
                NewOrderScreen(
                    onNavigateTo = { orderId, isOrdering, customerId ->
                        navController.navigate(
                            buildProductListRoute(
                                orderId,
                                isOrdering,
                                customerId
                            )
                        ) {
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                ) { from, to ->
                    navController.navigate(to) {
                        popUpTo("orderAppRoute") {
                            inclusive = true
                        }
                    }
                }
            }

            composable(DestinationCustomerList) {
                logBackStack(it, navController)
                CustomerScreen(
                    navigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToOrder = { customerId, orderId ->
                        navController.navigate(
                            buildNewOrderRoute(customerId, orderId)
                        )
                    },
                    onNavigateToCustomer = {
                        Log.d("TAG", "AppNavigation: $it")
                        navController.navigate(buildCustomerDetailsRoute(it))
                    },
                ) { from, to ->
                    navController.navigate(to) {
                        popUpTo(from) {
                            inclusive = true
                        }
                    }
                }
            }

            //New Customer Route
            composable(route = DestinationCustomerDetails) {
                CustomerDetailsScreen(
                    navigateFromTo = { from, to ->
                        navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }
                    },
                    permissionRequester = permissionRequester,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            //Existing Customer Route
            composable(DestinationCustomerDetailsRoute){
                CustomerDetailsScreen(
                    navigateFromTo = { from , to ->
                        navController.navigate(to) {
                            popUpTo(from) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    permissionRequester = permissionRequester
                )
            }

            composable(DestinationProductList){
                logBackStack(it, navController)
                ProductListScreen(
                    onNavigate = {
                        Log.d("Nav", "productId: $it") // Log the value
                        navController.navigate(buildProductDetailsRoute(it))
                    },
                    navigateFromTo = { from, to ->
                        navController.navigate(to) {
                            popUpTo(DestinationProductList) { inclusive = true }
                        }
                    },
                    navigateBack = { navController.popBackStack()},
                    navigateBackToOrder = { orderId, customerId ->
                        navController.navigate(buildNewOrderRoute(customerId, orderId)){
                            popUpTo(buildNewOrderRoute(customerId, orderId)) {
                                inclusive = true
                            }
                        }
                        //navController.navigate(buildNewOrderRoute(customerId = customerId, orderId = orderId))
                    }
                )
            }

            composable(DestinationProductListRoute) {
                logBackStack(it, navController)
                ProductListScreen(
                    onNavigate = {
                        Log.d("Nav", "productId: $it") // Log the value
                    },
                    navigateFromTo = { from, to ->
                        navController.navigate(to) {
                            popUpTo(DestinationProductListRoute) { inclusive = true }
                        }
                    },
                    navigateBack = { navController.popBackStack() },
                    navigateBackToOrder = { orderId, customerId ->
                        navController.popBackStack()
                    }

                )
            }

            //New Product Route
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

            //Exisitng Product Route
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

            composable(DestinationProfile){
                logBackStack(it, navController)
                ProfileScreen(
                    navigateFromTo = { from , to ->
                        navController.navigate(to)
                    }
                )
            }

        }
    }
}

fun buildProductDetailsRoute(argument: String) = "${DestinationProductDetails}/$argument"
fun buildCustomerDetailsRoute(argument: String) = "${DestinationCustomerDetails}/$argument"
fun buildNewOrderRoute(customerId: String, orderId: String) = "${DestinationNewOrder}/$customerId/$orderId"
//fun buildOrderDetailsRoute(customerId: String, orderId: String) = "${DestinationNewOrder}/$orderId/$customerId"
fun buildProductListRoute(orderId: String, isOrdering: String, customerId: String) = "${DestinationProductList}/$orderId/$isOrdering/$customerId"

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route

    if (navGraphRoute == null) {
        // Log a message indicating the parent destination has no route
        Log.d("ViewModel", "Parent destination has no route")
        return viewModel()
    }

    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return viewModel(parentEntry)
}

    fun logBackStack(backStackEntry: NavBackStackEntry, navController: NavController) {
        if (backStackEntry != null) {
            val backStackNames = navController
                .backQueue
                .map { it.destination.route }
                .joinToString(", ")

            Log.d("BackStackLog", "Back stack: $backStackNames")
        }
    }