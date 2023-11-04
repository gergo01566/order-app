package com.example.onlab.screen.product

data class ProductUiState (
    val id: String = "",
    var title: String = "",
    var pricePerPiece: String = "",
    val pricePerCarton: String = "",
    val category: String = "",
    val image: String = ""
)