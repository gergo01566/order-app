package com.example.orderapp.navigation

const val DestinationProductList = "ProductListScreen"
const val DestinationProductDetails = "ProductDetailsScreen"

const val DestinationCustomerList = "CustomerListScreen"
const val DestinationCustomerDetails = "CustomerDetailsScreen"

const val DestinationOrderList = "OrdersListScreen"
const val DestionationOrderDetails = "NewOrderScreen"

const val DestinationLogin = "LoginScreen"
const val DestinationProfile = "ProfileScreen"
const val DestinationEditProfile = "com.example.onlab.screen.profile.EditProfileScreen"

const val DestinationOneArg = "arg1"
const val DestinationTwoArg = "arg2"
const val DestinationThreeArg = "arg3"

const val DestinationProductDetailsRoute = "$DestinationProductDetails/{$DestinationOneArg}"
const val DestinationCustomerDetailsRoute = "$DestinationCustomerDetails/{$DestinationOneArg}"
const val DestinationOrderDetailsRoute = "$DestionationOrderDetails/{$DestinationOneArg}/{$DestinationTwoArg}"
const val DestinationProductListRoute = "$DestinationProductList/{$DestinationOneArg}/{$DestinationTwoArg}/{$DestinationThreeArg}"