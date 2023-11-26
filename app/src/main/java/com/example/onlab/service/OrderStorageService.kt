package com.example.onlab.service

import androidx.compose.runtime.MutableState
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderStorageService {

    fun getOrdersByStatus(status: MutableState<Int>): Flow<ValueOrException<List<Order>>>

    suspend fun getOrder(orderId: String): ValueOrException<Order>

    suspend fun addOrder(order: Order): ValueOrException<Boolean>

    suspend fun updateOrder(order: Order): ValueOrException<Boolean>

    suspend fun deleteOrder(orderId: String): ValueOrException<Boolean>
}