package com.example.onlab.screen.order

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.model.MOrder
import com.example.onlab.service.CustomerStorageService
import com.example.onlab.service.OrderStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersListViewModel @Inject constructor(
    private val orderStorageService: OrderStorageService,
    private val customerStorageService: CustomerStorageService
): OrderAppViewModel(){

    var ordersResponse by mutableStateOf<ValueOrException<List<MOrder>>>(ValueOrException.Loading)
        private set

    var deleteOrderResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var updateOrderResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var customersResponse by mutableStateOf<ValueOrException<List<MCustomer>>>(ValueOrException.Loading)
        private set

    var status = mutableStateOf(0)
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

        }
    }

    fun onUpdateOrder(order: MOrder, ){
        updateOrderResponse = ValueOrException.Loading
        launchCatching {
            try {
                updateOrderResponse = orderStorageService.updateOrder(order)
            } catch (e: Exception){
                updateOrderResponse = ValueOrException.Failure(e)
            }
        }
    }

    fun switchToCompletedOrIncompleteOrders(_status: Int){
        status.value = _status
        getOrders()
    }

    private fun getCustomers() {
        customersResponse = ValueOrException.Loading
        viewModelScope.launch {
            customerStorageService.getAllCustomers().collect { response ->
                customersResponse = response
            }
        }
    }
}