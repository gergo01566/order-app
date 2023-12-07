package com.example.onlab.model.service

import com.example.onlab.model.ValueOrException
import com.example.onlab.model.OrderItem
import kotlinx.coroutines.flow.Flow

interface OrderItemStorageService {

    fun getOrderItemsByOrderId(orderId: String): Flow<ValueOrException<List<OrderItem>>>

    suspend fun addOrderItem(orderItem: OrderItem): ValueOrException<Boolean>

    suspend fun updateOrderItem(orderItem: OrderItem): ValueOrException<Boolean>

    suspend fun deleteOrderItem(orderItemId: String): ValueOrException<Boolean>
}