package com.example.onlab.service

import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductStorageService {
    var searchQuery: String
    val category: Category?

    fun getAllProducts(): Flow<ValueOrException<List<Product>>>

    fun getProductsByCategoryAndText(text: String?, category: Category?): Flow<ValueOrException<List<Product>>>

    suspend fun getProduct(productId: String): ValueOrException<Product>

    suspend fun saveProduct(product: Product): ValueOrException<Boolean>

    suspend fun updateProduct(product: Product): ValueOrException<Boolean>

    suspend fun deleteProduct(productId: String): ValueOrException<Boolean>
}