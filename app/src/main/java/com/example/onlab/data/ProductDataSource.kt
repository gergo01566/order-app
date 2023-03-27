package com.example.onlab.data

import com.example.onlab.model.Category
import com.example.onlab.model.Product

class ProductDataSource {
    fun loadProducts(): List<Product> {
        return listOf(
            Product(title = "Milka", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka tejcsokis", pricePerPiece = 350, pricePerKarton = 8000,category = Category.Csokoládék),
            Product(title = "Milka epres", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka mogyorós", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka oreo", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka fehércsokis", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka dark chocolate", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka bubble", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka white bubble", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka tucc", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
            Product(title = "Milka tuccos cseresznyés barackos", pricePerPiece = 350, pricePerKarton = 8000, category = Category.Csokoládék),
        )
    }
}