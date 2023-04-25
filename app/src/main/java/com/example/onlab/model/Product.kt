package com.example.onlab.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "product_tbl")
data class Product(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "product_title")
    var title: String,

    @ColumnInfo(name = "product_category")
    val category: Category,

    @ColumnInfo(name = "product_piece_price")
    var pricePerPiece: Int,

    @ColumnInfo(name = "product_karton_price")
    var pricePerKarton: Int,

    @ColumnInfo(name = "product_image")
    val image: String,
){
    fun doesMatchSearchQuery(query: String): Boolean{
        val matchingCombinations = listOf("$title", "${title.first()}")
        return matchingCombinations.any{
            it.contains(query, ignoreCase = true)
        }
    }
}
