package com.example.onlab.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.time.LocalDate
import java.util.*

data class MOrder(
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

    @get:PropertyName("order_status")
    @set:PropertyName("order_status")
    var status: Int,

    @get:PropertyName("made_by")
    @set:PropertyName("made_by")
    var madeby: String,
){
    constructor() : this("", "", "", "", 0, "")
}
