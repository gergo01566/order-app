package com.example.onlab.repository

import com.example.onlab.data.ProductDatabaseDao
import com.example.onlab.model.Category
import com.example.onlab.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productDatabaseDao: ProductDatabaseDao){
    suspend fun addProduct(product: Product) = productDatabaseDao.insert(product)
    suspend fun updateProduct(product: Product) = productDatabaseDao.update(product)
    suspend fun deleteProduct(product: Product) = productDatabaseDao.deleteProduct(product)
    suspend fun deleteAllProducts() = productDatabaseDao.deleteAll()
    fun getAllProducts(): Flow<List<Product>> = productDatabaseDao.getAllProducts().flowOn(Dispatchers.IO).conflate()

}