package com.example.onlab.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "customer_tbl")
data class Customer(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "firstname")
    var firstName: String,

    @ColumnInfo(name = "lastname")
    var lastName: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "customer_image")
    val image: String,
){


    fun doesMatchSearchQuery(query: String): Boolean{
        val matchingCombinations = listOf("$firstName", "${firstName.first()}", "$lastName", "${lastName.first()}" )
        return matchingCombinations.any{
            it.contains(query, ignoreCase = true)
        }
    }
}

