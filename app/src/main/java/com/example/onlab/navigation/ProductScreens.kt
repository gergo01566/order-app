package com.example.onlab.navigation

enum class ProductScreens {
    ListScreen,
    DetailsScreen,
    NewProductScreen;
    companion object {
        fun fromRoute(route: String?): ProductScreens
                = when (route?.substringBefore("/")) {
            ListScreen.name -> ListScreen
            NewProductScreen.name -> NewProductScreen
            null -> ListScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}