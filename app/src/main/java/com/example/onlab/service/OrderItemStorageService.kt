package com.example.onlab.service

import androidx.compose.runtime.MutableState
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MOrder
import com.example.onlab.model.MOrderItem
import kotlinx.coroutines.flow.Flow

interface OrderItemStorageService {

    fun getOrderItemsByOrderId(orderId: String): Flow<ValueOrException<List<MOrderItem>>>

    suspend fun addOrderItem(orderItem: MOrderItem): ValueOrException<Boolean>

    suspend fun updateOrderItem(orderItem: MOrderItem): ValueOrException<Boolean>

    suspend fun deleteOrderItem(orderItemId: String): ValueOrException<Boolean>
}