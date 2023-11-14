package com.example.onlab.repository

import com.example.onlab.model.MOrderItem

interface OrderItemsRepository {
    suspend fun getOrderItemsFromNetwork(orders: List<MOrderItem>)
    suspend fun initOrderItems()
    suspend fun insertOrderItem(orderItem: MOrderItem)
    suspend fun deleteOrderItem(orderItem: MOrderItem)
    fun getOrderItems(): List<MOrderItem>
    suspend fun updateOrderItem(updatedOrderItem: MOrderItem)
}