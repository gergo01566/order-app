package com.example.onlab.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity(tableName = "order_tbl")
data class Order(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "order_date")
    var date: LocalDate,

    @ColumnInfo(name = "customer_id")
    val customerID: UUID,

    @ColumnInfo(name = "status_id")
    var statusID: UUID,
)


