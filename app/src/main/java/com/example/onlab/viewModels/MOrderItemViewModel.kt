package com.example.onlab.viewModels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.onlab.model.MOrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


class MOrderItemViewModel : ViewModel() {

     //New variable to store order items
    private val _sharedOrderItemList = MutableStateFlow<List<MOrderItem>>(emptyList())
    val sharedOrderItemList = _sharedOrderItemList.asStateFlow()

    // Function to get the list of order items
    fun getOrderItemsList(): List<MOrderItem> {
        return _sharedOrderItemList.value
    }

    // Function to add an order item to the list
    fun addItemToOrderList(newOrderItem: MOrderItem) {
        val currentList = _sharedOrderItemList.value
        val updatedList = currentList.toMutableList()
        updatedList.add(newOrderItem)
        _sharedOrderItemList.value = updatedList
        Log.d("logg", "addItemToOrderList: ${_sharedOrderItemList.value.size}")
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel cleared")
    }

    // Function to remove an order item from the list
    fun removeOrderItem(orderItem: MOrderItem) {
        val currentList = _sharedOrderItemList.value
        val updatedList = currentList.toMutableList()
        updatedList.remove(orderItem)
        _sharedOrderItemList.value = updatedList
    }
//
//    // Function to update an order item in the list
    fun updateOrderItem(orderItemToUpdate: MOrderItem) {
        val currentList = _sharedOrderItemList.value
        val updatedList = currentList.toMutableList()
        val index = updatedList.indexOfFirst {
            it.id == orderItemToUpdate.id
        }
        if (index != -1) {
            Log.d("DB", "siker")
            updatedList[index] = orderItemToUpdate
        }
        _sharedOrderItemList.value = updatedList
    }
//
//    // Function to clear the list
//    fun clearOrderItemsList() {
//        _sharedOrderItemList.clear()
//    }

}