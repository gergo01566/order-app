package com.example.onlab.model

import com.example.onlab.screens.product_details.ProductUiState
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.*

data class Product(
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
    constructor(productUiState: ProductUiState) : this(
        productUiState.id,
        productUiState.title,
        productUiState.category,
        productUiState.pricePerPiece.toInt(),
        productUiState.pricePerCarton.toInt(),
        productUiState.image
    )
    constructor() : this("", "", "", 0, 0, "")

    fun doesMatchSearchQuery(query: String): Boolean{
        val matchingCombinations = listOf(title, "${title.first()}")
        return matchingCombinations.any{
            it.contains(query, ignoreCase = true)
        }
    }
}
