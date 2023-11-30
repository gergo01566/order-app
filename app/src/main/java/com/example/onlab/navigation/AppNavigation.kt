@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.onlab.navigation

import AppState
import EditProfileScreen
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
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)

fun NavGraphBuilder.appNavigation(appState: AppState) {
//    val permissionRequester = PermissionRequester()
//
//    val startDestination : String = if(FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
//        DestinationLogin
//    } else {
//        DestinationCustomerList
//    }
//
//    NavHost(
//        navController = appState.navController,
//        startDestination = "orderAppRoute"
//    ) {
//        navigation(
//            startDestination = startDestination,
//            route = "orderAppRoute"
//        ) {

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
                    navigateBack = { appState.navigateBack()}
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
                ) { from, to ->
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
                    navigateBack = {
                        appState.navigateBack()
                    },
                    onNavigateToOrder = { customerId, orderId ->
                        appState.navController.navigate(
                            buildNewOrderRoute(customerId, orderId)
                        )
                    },
                    onNavigateToCustomer = {
                        Log.d("TAG", "AppNavigation: $it")
                        appState.navController.navigate(buildCustomerDetailsRoute(it))
                    },
                ) { from, to ->
                    appState.navController.navigate(to) {
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
                        appState.navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }
                    },
                    permissionRequester = appState.permissionRequester,
                    onNavigateBack = {
                        appState.navigateBack()
                    }
                )
            }

            //Existing Customer Route
            composable(DestinationCustomerDetailsRoute){
                CustomerDetailsScreen(
                    navigateFromTo = { from , to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateBack = { appState.navigateBack() },
                    permissionRequester = appState.permissionRequester
                )
            }

            composable(DestinationProductList){
                logBackStack(it, appState.navController)
                ProductListScreen(
                    onNavigate = {
                        Log.d("Nav", "productId: $it") // Log the value
                        appState.navController.navigate(buildProductDetailsRoute(it))
                    },
                    navigateFromTo = { from, to ->
                        appState.navController.navigate(to) {
                            popUpTo(DestinationProductList) { inclusive = true }
                        }
                    },
                    navigateBack = { appState.navigateBack() },
                    navigateBackToOrder = { orderId, customerId ->
                        appState.navController.navigate(buildNewOrderRoute(customerId, orderId)){
                            popUpTo(buildNewOrderRoute(customerId, orderId)) {
                                inclusive = true
                            }
                        }
                        //navController.navigate(buildNewOrderRoute(customerId = customerId, orderId = orderId))
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
                    navigateBack = { appState.navigateBack() },
                    navigateBackToOrder = { orderId, customerId ->
                        appState.navController.popBackStack()
                    }

                )
            }

            //New Product Route
            composable(route = DestinationProductDetails) {
                ProductDetailsScreen(
                    navigateFromTo = { from, to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) { inclusive = true }
                        }
                    },
                    permissionRequester = appState.permissionRequester,
                    navigateBack = {
                        appState.navigateBack()
                    }
                )
            }

            //Exisitng Product Route
            composable(DestinationProductDetailsRoute){
                ProductDetailsScreen(
                    navigateFromTo = { from , to ->
                        appState.navController.navigate(to) {
                            popUpTo(from) {
                                inclusive = true
                            }
                        }
                    },
                    navigateBack = { appState.navigateBack() },
                    permissionRequester = appState.permissionRequester
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
        val backStackNames = navController
            .backQueue
            .map { it.destination.route }
            .joinToString(", ")

        Log.d("BackStackLog", "Back stack: $backStackNames")
    }