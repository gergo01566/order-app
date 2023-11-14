package com.example.onlab.repository

import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MOrderItem
import com.example.onlab.service.OrderItemStorageService
import kotlinx.coroutines.flow.collect

class MemoryOrderItemRepository : OrderItemsRepository {
    private var orderItems = mutableListOf<MOrderItem>()

    override suspend fun getOrderItemsFromNetwork(orders: List<MOrderItem>) {
        initOrderItems()
        orderItems = orders as MutableList<MOrderItem>
    }

    override suspend fun initOrderItems() {
        orderItems.clear()
    }

    override suspend fun insertOrderItem(orderItem: MOrderItem) {
        orderItems.add(orderItem)
    }

    override suspend fun deleteOrderItem(orderItem: MOrderItem) {
        orderItems.remove(orderItem)
    }

    override fun getOrderItems(): List<MOrderItem> {
        return orderItems
    }

    override suspend fun updateOrderItem(updatedOrderItem: MOrderItem) {
        val index = orderItems.indexOfFirst { it.id == updatedOrderItem.id }
        if (index != -1) {
            orderItems[index] = updatedOrderItem
        }
    }
}