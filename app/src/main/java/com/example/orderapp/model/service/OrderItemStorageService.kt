package com.example.orderapp.model.service

import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.OrderItem
import kotlinx.coroutines.flow.Flow

interface OrderItemStorageService {

    fun getOrderItemsByOrderId(orderId: String): Flow<ValueOrException<List<OrderItem>>>

    suspend fun addOrderItem(orderItem: OrderItem): ValueOrException<Boolean>

    suspend fun updateOrderItem(orderItem: OrderItem): ValueOrException<Boolean>

    suspend fun deleteOrderItem(orderItemId: String): ValueOrException<Boolean>
}