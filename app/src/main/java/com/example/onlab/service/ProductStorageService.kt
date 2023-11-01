package com.example.onlab.service

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.onlab.data.DataOrException
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.MCustomer
import com.example.onlab.model.MProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ProductStorageService {
    var searchQuery: String
    val category: Category?

    val data: MutableStateFlow<DataOrException<List<MProduct>, Boolean, Exception>>

    fun getAllProducts(): Flow<ValueOrException<List<MProduct>>>

    fun getProductsByCategoryAndText(text: String?, category: Category?): Flow<ValueOrException<List<MProduct>>>

    suspend fun getProduct(productId: String): ValueOrException<MProduct>

    suspend fun saveProduct(product: MProduct, onComplete: () -> Unit): ValueOrException<Boolean>

    suspend fun updateProduct(product: MProduct, onComplete: () -> Unit): ValueOrException<Boolean>

    suspend fun deleteProduct(productId: String, onComplete: () -> Unit): ValueOrException<Boolean>
}