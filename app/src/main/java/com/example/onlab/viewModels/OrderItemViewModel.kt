package com.example.onlab.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.model.OrderItem
import com.example.onlab.model.Product
import com.example.onlab.repository.OrderItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

import javax.inject.Inject

@HiltViewModel
class OrderItemViewModel @Inject constructor(private val repository: OrderItemRepository) : ViewModel() {
    private val orderItemlist = MutableStateFlow<List<OrderItem>>(emptyList())

    init {
        //making sure our highway has a lot of different lanes
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllOrderItem().distinctUntilChanged().
            collect{ listOfOrderItems ->
                if (listOfOrderItems.isNullOrEmpty()){
                    Log.d("Empty", "Empty list")
                } else {
                    orderItemlist.value = listOfOrderItems
                }
            }
        }
    }

    fun addOrderItem(orderItem: OrderItem) = viewModelScope.launch { repository.addOrderItem(orderItem) }
    fun updateOrderItem(orderItem: OrderItem) = viewModelScope.launch { repository.updateOrderItem(orderItem) }
    fun deleteOrderItem(orderItem: OrderItem) = viewModelScope.launch { repository.deleteOrderItem(orderItem) }
    fun getOrderItemsByOrder(orderID: String): List<OrderItem> {
        return orderItemlist.value.filter {
            it.orderID.toString() == orderID
        }
    }
}