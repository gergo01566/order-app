package com.example.onlab.service

import com.example.onlab.model.Category
import com.example.onlab.model.MProduct
import kotlinx.coroutines.flow.Flow

interface ProductStorageService {
    val products: Flow<List<MProduct>>
    var searchQuery: String
    val category: Category?

    suspend fun getProduct(productId: String): MProduct?

    suspend fun saveProduct(product: MProduct)

    suspend fun updateProduct(product: MProduct)

    suspend fun deleteProduct(productId: String)

}