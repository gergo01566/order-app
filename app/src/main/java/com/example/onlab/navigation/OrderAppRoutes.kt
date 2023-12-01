package com.example.onlab.navigation

const val DestinationProductList = "ProductListScreen"
const val DestinationProductDetails = "ProductDetailsScreen"
const val DestinationNewProduct = "NewProductScreen"

const val DestinationCustomerList = "CustomerListScreen"
const val DestinationCustomerDetails = "CustomerDetailsScreen"
const val DestinationNewCustomer = "NewCustomerScreen"

const val DestinationOrderList = "OrdersListScreen"
const val DestinationNewOrder = "NewOrderScreen"

const val DestinationLogin = "LoginScreen"
const val DestinationProfile = "ProfileScreen"
const val DestinationEditProfile = "com.example.onlab.screen.profile.EditProfileScreen"


const val DestinationOneArg = "arg1"
const val DestinationTwoArg = "arg2"
const val DestinationThreeArg = "arg3"
const val DestinationProductDetailsRoute = "$DestinationProductDetails/{$DestinationOneArg}"
const val DestinationCustomerDetailsRoute = "$DestinationCustomerDetails/{$DestinationOneArg}"
const val DestinationEditProfileRoute = "$DestinationEditProfile/{$DestinationOneArg}"
const val DestinationOrderDetailsRoute = "$DestinationNewOrder/{$DestinationOneArg}/{$DestinationTwoArg}"
const val DestinationProductListRoute = "$DestinationProductList/{$DestinationOneArg}/{$DestinationTwoArg}/{$DestinationThreeArg}"