package com.example.orderapp.model

import com.example.orderapp.screens.customer_details.CustomerUiState
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.*

data class Customer(
    @Exclude
    val id: String? = null,

    @get:PropertyName("first_name")
    @set:PropertyName("first_name")
    var firstName: String,

    @get:PropertyName("last_name")
    @set:PropertyName("last_name")
    var lastName: String,

    @get:PropertyName("customer_address")
    @set:PropertyName("customer_address")
    var address: String,

    @get:PropertyName("phone_number")
    @set:PropertyName("phone_number")
    var phoneNumber: String,

    @get:PropertyName("customer_image")
    @set:PropertyName("customer_image")
    var image: String,
)
{
    constructor(customerUiState: CustomerUiState) : this(
        customerUiState.id,
        customerUiState.firstName,
        customerUiState.lastName,
        customerUiState.address,
        customerUiState.phoneNumber,
        customerUiState.image
    )

    constructor() : this("", "", "", "", "", "")
    fun doesMatchSearchQuery(query: String): Boolean{
        val matchingCombinations = listOf(firstName, "${firstName.first()}", lastName, "${lastName.first()}" )
        return matchingCombinations.any{
            it.contains(query, ignoreCase = true)
        }
    }
}
