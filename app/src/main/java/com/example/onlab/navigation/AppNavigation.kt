@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.onlab.navigation

import com.example.onlab.AppState
import com.example.onlab.screens.edit_profile.EditProfileScreen
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.example.onlab.screens.customer_details.CustomerDetailsScreen

import com.example.onlab.screens.product.ProductListScreen
import com.example.onlab.screens.customers.CustomerScreen
import com.example.onlab.screens.order_details.NewOrderScreen
import com.example.onlab.screens.order.OrdersScreen
import com.example.onlab.screens.product_details.ProductDetailsScreen
import com.example.onlab.screens.profile.ProfileScreen
import com.example.onlab.screens.login.LoginScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)

fun NavGraphBuilder.appNavigation(appState: AppState) {

            composable(DestinationLogin){
                LoginScreen(
                    navigateFromTo = { from , to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }},
                )
            }

            composable(DestinationEditProfile){
                logBackStack(it, appState.navController)
                EditProfileScreen(
                    applicationContext = appState.context,
                    onNavigateBack = { appState.navigateBack() },
                    navigateFromTo = { from , to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }},
                )
            }

            composable(DestinationOrderList){
                OrdersScreen(
                    onNavigate = { orderId, customerId ->
                        appState.navController.navigate(buildNewOrderRoute(customerId = customerId, orderId = orderId))
                    },
                ){ from, to ->
                    appState.navController.navigate(to) {
                        popUpTo(from) { inclusive = true }
                    }
                }
            }

            composable(DestinationOrderDetailsRoute) {
                logBackStack(it, appState.navController)
                NewOrderScreen(
                    onNavigateTo = { orderId, isOrdering, customerId ->
                        appState.navController.navigate(
                            buildProductListRoute(
                                orderId,
                                isOrdering,
                                customerId
                            )
                        ) {
                        }
                    },
                    onNavigateBack = { appState.navigateBack() },
                ) { _, to ->
                    appState.navController.navigate(to) {
                        popUpTo("orderAppRoute") {
                            inclusive = true
                        }
                    }
                }
            }

            composable(DestinationCustomerList) {
                logBackStack(it, appState.navController)
                CustomerScreen(
                    onNavigateToOrder = { customerId, orderId ->
                        appState.navController.navigate(
                            buildNewOrderRoute(customerId, orderId)
                        )
                    },
                    navigateInBottomBar = {
                        appState.navController.navigate(it) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToCustomer = { customerId ->
                        appState.navController.navigate(buildCustomerDetailsRoute(customerId))
                    },
                ) { from, to ->
                    appState.navController.navigate(to) {

                    }
                }
            }

            //New Customer Route
            composable(route = DestinationCustomerDetails) {
                CustomerDetailsScreen(
                    context = appState.context,
                    navigateFromTo = { from, to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        appState.navigateBack()
                    }
                )
            }

            //Existing Customer Route
            composable(DestinationCustomerDetailsRoute){
                CustomerDetailsScreen(
                    context = appState.context,
                    navigateFromTo = { from , to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateBack = { appState.navigateBack() },
                )
            }

            composable(DestinationProductList){
                logBackStack(it, appState.navController)
                ProductListScreen(
                    onNavigate = {
                        appState.navController.navigate(buildProductDetailsRoute(it))
                    },
                    navigateFromTo = { from, to ->
                        appState.navController.navigate(to)
                    },
                    navigateBackToOrder = { orderId, customerId ->
                        appState.navController.navigate(buildNewOrderRoute(customerId, orderId)){
                            popUpTo(buildNewOrderRoute(customerId, orderId)) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(DestinationProductListRoute) {
                logBackStack(it, appState.navController)
                ProductListScreen(
                    onNavigate = {
                        Log.d("Nav", "productId: $it") // Log the value
                    },
                    navigateFromTo = { from, to ->
                        appState.navController.navigate(to) {
                            popUpTo(DestinationProductListRoute) { inclusive = true }
                        }
                    },
                    navigateBackToOrder = { orderId, customerId ->
                        appState.navController.popBackStack()
                    }

                )
            }

            //New Product Route
            composable(route = DestinationProductDetails) {
                ProductDetailsScreen(
                    applicationContext = appState.context,
                    navigateFromTo = { from, to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }
                    },
                    navigateBack = {
                        appState.navigateBack()
                    }
                )
            }

            //Exisitng Product Route
            composable(DestinationProductDetailsRoute){
                ProductDetailsScreen(
                    applicationContext = appState.context,
                    navigateFromTo = { from , to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) {
                                inclusive = true
                            }
                        }
                    },
                    navigateBack = { appState.navigateBack() },
                )
            }

            composable(DestinationProfile){
                logBackStack(it, appState.navController)
                ProfileScreen(
                    navigateFromTo = { from , to ->
                        appState.navController.navigate(to)
                    }
                )
            }



}

fun buildProductDetailsRoute(argument: String) = "${DestinationProductDetails}/$argument"
fun buildCustomerDetailsRoute(customerId: String) = "${DestinationCustomerDetails}/$customerId"
fun buildNewOrderRoute(customerId: String, orderId: String) = "${DestionationOrderDetails}/$customerId/$orderId"
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
        val backStackNames = navController
            .backQueue
            .map { it.destination.route }
            .joinToString(", ")

        Log.d("BackStackLog", "Back stack: $backStackNames")
    }