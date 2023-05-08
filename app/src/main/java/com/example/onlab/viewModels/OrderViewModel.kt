package com.example.onlab.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.model.Order
import com.example.onlab.model.OrderItem
import com.example.onlab.repository.OrderItemRepository
import com.example.onlab.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val repository: OrderRepository) : ViewModel() {
    private val orderList = MutableStateFlow<List<Order>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllOrder().distinctUntilChanged().
            collect{ listOfOrders ->
                if (listOfOrders.isNullOrEmpty()){
                    Log.d("Empty", "Empty list")
                } else {
                    orderList.value = listOfOrders
                }
            }
        }
    }

    fun addOrder(order: Order) = viewModelScope.launch { repository.addOrder(order) }
    fun updateOrder(order: Order) = viewModelScope.launch { repository.updateOrder(order) }
    fun deleteOrder(order: Order) = viewModelScope.launch { repository.deleteOrder(order) }
    fun getOrdersByStatus(status: Int): List<Order> {
        return orderList.value.filter {
            it.status == status
        }
    }

}
