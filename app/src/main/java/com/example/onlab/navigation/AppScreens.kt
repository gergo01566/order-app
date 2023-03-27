package com.example.onlab.navigation

enum class AppScreens {
    ProductScreen,
    CustomerScreen;
    companion object {
        fun fromRoute(route: String?): AppScreens
                = when (route?.substringBefore("/")) {
            ProductScreen.name -> ProductScreen
            CustomerScreen.name -> CustomerScreen
            null -> ProductScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}