package com.example.orderapp.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.*

data class Order(
    @Exclude
    val id: String? = null,

    @get:PropertyName("order_id")
    @set:PropertyName("order_id")
    var orderId: String? = null,

    @get:PropertyName("order_date")
    @set:PropertyName("order_date")
    var date: String,

    @get:PropertyName("customer_id")
    @set:PropertyName("customer_id")
    var customerID: String,

    @get:PropertyName("is_completed")
    @set:PropertyName("is_completed")
    var isCompleted: Boolean,

    @get:PropertyName("made_by")
    @set:PropertyName("made_by")
    var madeby: String,
){
    constructor() : this("", "", "", "", false, "")
}
