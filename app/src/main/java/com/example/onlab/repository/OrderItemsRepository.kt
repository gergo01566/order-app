package com.example.onlab.repository

import com.example.onlab.model.OrderItem

interface OrderItemsRepository {
    suspend fun getOrderItemsFromNetwork(orders: List<OrderItem>)
    suspend fun initOrderItems()
    suspend fun insertOrderItem(orderItem: OrderItem)
    suspend fun deleteOrderItem(orderItem: OrderItem)
    fun getOrderItems(): List<OrderItem>
    suspend fun updateOrderItem(updatedOrderItem: OrderItem)
}