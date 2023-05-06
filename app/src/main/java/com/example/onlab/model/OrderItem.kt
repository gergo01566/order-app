package com.example.onlab.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "order_item_tbl")
data class OrderItem(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "amount")
    var amount: Int,

    @ColumnInfo(name = "orderID")
    var orderID: UUID,

    @ColumnInfo(name = "productID")
    var productID: UUID,

    @ColumnInfo(name = "statusID")
    var statusID: Int,

    @ColumnInfo(name = "karton")
    val karton: Boolean,

    @ColumnInfo(name = "db")
    var db: Boolean,
)
