package com.example.onlab.model

import java.util.UUID

data class Product(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val category: Category,
    val pricePerPiece: Int,
    val pricePerKarton: Int,
    val image: String = "https://images-na.ssl-images-amazon.com/images/M/MV5BNzM2MDk3MTcyMV5BMl5BanBnXkFtZTcwNjg0MTUzNA@@._V1_SX1777_CR0,0,1777,999_AL_.jpg"
)
