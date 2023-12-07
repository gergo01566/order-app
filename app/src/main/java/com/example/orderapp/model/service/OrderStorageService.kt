package com.example.orderapp.model.service

import androidx.compose.runtime.MutableState
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderStorageService {

    fun getOrdersByStatus(status: MutableState<Boolean>): Flow<ValueOrException<List<Order>>>

    suspend fun getOrder(orderId: String): ValueOrException<Order>

    suspend fun addOrder(order: Order): ValueOrException<Boolean>

    suspend fun updateOrder(order: Order): ValueOrException<Boolean>

    suspend fun deleteOrder(orderId: String): ValueOrException<Boolean>
}