package com.example.onlab.screen.product

import androidx.lifecycle.ViewModel
import com.example.onlab.data.ProductDataSource
import com.example.onlab.model.Category
import com.example.onlab.model.Product

class ProductViewModel : ViewModel() {
    private var productList = mutableListOf<Product>()

    init {
        productList.addAll(ProductDataSource().loadProducts())
    }

    fun addProduct(){
        productList.add(Product(title = "uj elem", category = Category.Cukorkák, pricePerKarton = 29, pricePerPiece = 2))
    }

    fun removeProduct(product: Product){
        productList.remove(product)
    }

    fun getAllProduct(): List<Product>{
        return productList
    }

    fun getProductByCategory(category: Category): List<Product>{
        return productList.filter { product -> product.category == category}
    }
}