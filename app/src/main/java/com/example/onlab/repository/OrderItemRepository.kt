package com.example.onlab.repository

import com.example.onlab.data.OrderItemDatabaseDao
import com.example.onlab.data.ProductDatabaseDao
import com.example.onlab.model.Order
import com.example.onlab.model.OrderItem
import com.example.onlab.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OrderItemRepository @Inject constructor(private val orderItemDatabaseDao: OrderItemDatabaseDao){
    suspend fun addOrderItem(orderItem: OrderItem) = orderItemDatabaseDao.insert(orderItem)
    suspend fun updateOrderItem(orderItem: OrderItem) = orderItemDatabaseDao.update(orderItem)
    suspend fun deleteOrderItem(orderItem: OrderItem) = orderItemDatabaseDao.deleteOrderItem(orderItem)
    fun deleteAllOrderItem() = orderItemDatabaseDao.getAllOrderItem()
    fun getAllOrderItem(): Flow<List<OrderItem>> = orderItemDatabaseDao.getAllOrderItem().flowOn(Dispatchers.IO).conflate()
}