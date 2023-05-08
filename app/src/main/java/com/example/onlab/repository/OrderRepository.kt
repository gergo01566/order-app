package com.example.onlab.repository

import com.example.onlab.data.OrderDatabaseDao
import com.example.onlab.data.ProductDatabaseDao
import com.example.onlab.model.Order
import com.example.onlab.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OrderRepository @Inject constructor(private val orderDatabaseDao: OrderDatabaseDao){
    suspend fun addOrder(order: Order) = orderDatabaseDao.insert(order)
    suspend fun updateOrder(order: Order) = orderDatabaseDao.update(order)
    suspend fun deleteOrder(order: Order) = orderDatabaseDao.deleteOrder(order)
    suspend fun deleteAllOrder() = orderDatabaseDao.deleteAll()
    fun getAllOrder(): Flow<List<Order>> = orderDatabaseDao.getAllOrder().flowOn(Dispatchers.IO).conflate()
}