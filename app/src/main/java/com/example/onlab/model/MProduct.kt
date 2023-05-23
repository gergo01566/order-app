package com.example.onlab.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.*

data class MProduct(
    @Exclude
    val id: String? = null,

    @get:PropertyName("product_title")
    @set:PropertyName("product_title")
    var title: String,

    @get:PropertyName("product_category")
    @set:PropertyName("product_category")
    var category: String,

    @get:PropertyName("price_piece")
    @set:PropertyName("price_piece")
    var pricePerPiece: Int,

    @get:PropertyName("price_carton")
    @set:PropertyName("price_carton")
    var pricePerKarton: Int,

    @get:PropertyName("product_image")
    @set:PropertyName("product_image")
    var image: String,
){
    constructor() : this("", "", "", 0, 0, "")

    fun doesMatchSearchQuery(query: String): Boolean{
        val matchingCombinations = listOf("$title", "${title.first()}")
        return matchingCombinations.any{
            it.contains(query, ignoreCase = true)
        }
    }
}