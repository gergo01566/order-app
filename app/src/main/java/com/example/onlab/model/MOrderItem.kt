package com.example.onlab.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.*

data class MOrderItem(
    @Exclude
    val id: String? = null,

    @get:PropertyName("item_amount")
    @set:PropertyName("item_amount")
    var amount: Int,

    @get:PropertyName("order_id")
    @set:PropertyName("order_id")
    var orderID: String,

    @get:PropertyName("product_id")
    @set:PropertyName("product_id")
    var productID: String,

    @get:PropertyName("status_id")
    @set:PropertyName("status_id")
    var statusID: Int,

    @get:PropertyName("is_karton")
    @set:PropertyName("is_karton")
    var carton: Boolean,

    @get:PropertyName("is_piece")
    @set:PropertyName("is_piece")
    var piece: Boolean,
){
    constructor() : this("", 0, "", "", 0, false, false)
}
