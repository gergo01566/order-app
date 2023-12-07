package com.example.onlab.model.repository

import com.example.onlab.model.OrderItem

class MemoryOrderItemRepositoryImp : OrderItemsRepository {
    private var orderItems = mutableListOf<OrderItem>()

    override suspend fun getOrderItemsFromNetwork(orders: List<OrderItem>) {
        initOrderItems()
        orderItems = orders as MutableList<OrderItem>
    }

    override suspend fun initOrderItems() {
        orderItems.clear()
    }

    override suspend fun insertOrderItem(orderItem: OrderItem) {
        orderItems.add(orderItem)
    }

    override suspend fun deleteOrderItem(orderItem: OrderItem) {
        orderItems.remove(orderItem)
    }

    override fun getOrderItems(): List<OrderItem> {
        return orderItems
    }

    override suspend fun updateOrderItem(updatedOrderItem: OrderItem) {
        val index = orderItems.indexOfFirst { it.id == updatedOrderItem.id }
        if (index != -1) {
            orderItems[index] = updatedOrderItem
        }
    }
}