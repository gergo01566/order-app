package com.example.orderapp.screens.order

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.orderapp.R
import com.example.orderapp.common.snackbar.SnackbarManager
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.Customer
import com.example.orderapp.model.Order
import com.example.orderapp.model.service.CustomerStorageService
import com.example.orderapp.model.service.OrderStorageService
import com.example.orderapp.screens.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersListViewModel @Inject constructor(
    private val orderStorageService: OrderStorageService,
    private val customerStorageService: CustomerStorageService
): OrderAppViewModel(){

    var ordersResponse by mutableStateOf<ValueOrException<List<Order>>>(ValueOrException.Loading)
        private set

    var deleteOrderResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var updateOrderResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var customersResponse by mutableStateOf<ValueOrException<List<Customer>>>(ValueOrException.Loading)
        private set

    var status = mutableStateOf(false)
        private set

    init {
        getOrders()
        getCustomers()
    }

    private fun getOrders(){
        launchCatching {
            ordersResponse = ValueOrException.Loading
            orderStorageService.getOrdersByStatus(status).collect{ response ->
                ordersResponse = response
            }
            when (ordersResponse){
                is ValueOrException.Failure -> SnackbarManager.displayMessage(R.string.data_loading_error)
                else -> Unit
            }
        }
    }

    fun onDeleteOrder(orderId: String, onComplete: () -> Unit){
        deleteOrderResponse = ValueOrException.Loading
        launchCatching {
            try {
                deleteOrderResponse = orderStorageService.deleteOrder(orderId)
                onComplete()
            } catch (e: java.lang.Exception){
                deleteOrderResponse = ValueOrException.Failure(e)
            }
            when(deleteOrderResponse){
                is ValueOrException.Success -> SnackbarManager.displayMessage(R.string.save_success)
                is ValueOrException.Failure -> SnackbarManager.displayMessage(R.string.delete_error)
                else -> {}
            }

        }
    }

    fun onUpdateOrder(order: Order){
        updateOrderResponse = ValueOrException.Loading
        launchCatching {
            updateOrderResponse = try {
                orderStorageService.updateOrder(order)
            } catch (e: Exception){
                ValueOrException.Failure(e)
            }
        }
        when(updateOrderResponse){
            is ValueOrException.Success -> SnackbarManager.displayMessage(R.string.save_success)
            is ValueOrException.Failure -> SnackbarManager.displayMessage(R.string.save_error)
            else -> {}
        }
    }

    fun switchToCompletedOrIncompleteOrders(_status: Int){
        status.value = _status != 0
        getOrders()
    }

    private fun getCustomers() {
        customersResponse = ValueOrException.Loading
        viewModelScope.launch {
            customerStorageService.getAllCustomers().collect { response ->
                customersResponse = response
            }
        }
        when (customersResponse){
            is ValueOrException.Failure -> SnackbarManager.displayMessage(R.string.data_loading_error)
            else -> Unit
        }
    }

}


