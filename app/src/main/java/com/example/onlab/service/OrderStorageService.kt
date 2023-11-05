package com.example.onlab.service

import androidx.compose.runtime.MutableState
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MOrder
import kotlinx.coroutines.flow.Flow

interface OrderStorageService {

    fun getOrdersByStatus(status: MutableState<Int>): Flow<ValueOrException<List<MOrder>>>

    suspend fun getOrder(orderId: String): ValueOrException<MOrder>

    suspend fun addOrder(order: MOrder): ValueOrException<Boolean>

    suspend fun updateOrder(order: MOrder): ValueOrException<Boolean>

    suspend fun deleteOrder(orderId: String): ValueOrException<Boolean>
}