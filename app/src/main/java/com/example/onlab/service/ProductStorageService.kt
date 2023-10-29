package com.example.onlab.service

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.onlab.data.DataOrException
import com.example.onlab.model.Category
import com.example.onlab.model.MCustomer
import com.example.onlab.model.MProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ProductStorageService {
    val products: Flow<List<MProduct>>
    var searchQuery: String
    val category: Category?
    val loading : Flow<Boolean>

    suspend fun getProduct(productId: String): MProduct?

    suspend fun saveProduct(product: MProduct, onComplete: () -> Unit)

    suspend fun updateProduct(product: MProduct, onComplete: () -> Unit)

    suspend fun deleteProduct(productId: String, onComplete: () -> Unit)

}