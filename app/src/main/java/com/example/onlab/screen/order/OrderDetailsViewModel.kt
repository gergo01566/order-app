package com.example.onlab.screen.order

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MOrderItem
import com.example.onlab.model.MProduct
import com.example.onlab.navigation.DestinationOneArg
import com.example.onlab.navigation.DestinationTwoArg
import com.example.onlab.service.OrderItemStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderItemStorageService: OrderItemStorageService,
    savedStateHandle: SavedStateHandle
): OrderAppViewModel() {

    var orderItemsResponse by mutableStateOf<ValueOrException<List<MOrderItem>>>(ValueOrException.Loading)
        private set

    var deleteOrderItemResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var updateOrderItemResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var saveOrderItemResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    val customerId = savedStateHandle.get<String>(DestinationOneArg)
    val orderId = savedStateHandle.get<String>(DestinationTwoArg)

    init {
        launchCatching {
            if (!orderId.isNullOrEmpty()){
                try {
                    orderItemsResponse = ValueOrException.Loading
                    orderItemStorageService.getOrderItemsByOrderId(orderId).collect{ response->
                        orderItemsResponse = response
                    }
                } catch (e: java.lang.Exception){
                    orderItemsResponse = ValueOrException.Failure(e)
                }
            }
        }
    }
}