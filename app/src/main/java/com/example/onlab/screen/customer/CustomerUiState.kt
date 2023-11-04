package com.example.onlab.screen.customer

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class CustomerUiState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val image: String = "",
)