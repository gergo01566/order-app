package com.example.onlab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.onlab.screen.ProductListScreen
import com.example.onlab.screen.product.NewProductScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ProductScreens.ListScreen.name){
        composable(ProductScreens.ListScreen.name){
            ProductListScreen(navController = navController)
        }
        composable(ProductScreens.NewProductScreen.name)
        {
            NewProductScreen(navController = navController)
        }
        composable(ProductScreens.NewProductScreen.name + "/{product}",
        arguments = listOf(navArgument(name = "product"){type = NavType.StringType})
        ){ navBackStackEntry ->  
            NewProductScreen(navController = navController, navBackStackEntry.arguments?.getString("product"))
        }
    }
}