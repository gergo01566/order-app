package com.example.onlab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlab.screen.ProductListScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.ProductScreen.name){
        composable(AppScreens.ProductScreen.name){
            ProductListScreen()
        }
        composable(AppScreens.CustomerScreen.name){

        }
    }
}