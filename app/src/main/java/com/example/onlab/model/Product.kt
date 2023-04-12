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
    val image: String = "https://images-na.ssl-images-amazon.com/images/M/MV5BNzM2MDk3MTcyMV5BMl5BanBnXkFtZTcwNjg0MTUzNA@@._V1_SX1777_CR0,0,1777,999_AL_.jpg"
)
