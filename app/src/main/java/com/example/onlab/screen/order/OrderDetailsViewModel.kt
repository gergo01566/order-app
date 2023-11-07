package com.example.onlab.screen.order

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MOrder
import com.example.onlab.model.MOrderItem
import com.example.onlab.model.MProduct
import com.example.onlab.navigation.DestinationOneArg
import com.example.onlab.navigation.DestinationTwoArg
import com.example.onlab.service.OrderItemStorageService
import com.example.onlab.service.OrderStorageService
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderItemStorageService: OrderItemStorageService,
    private val orderStorageService: OrderStorageService,
    private val productStorageService: ProductStorageService,
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

    var saveOrderToFirebase by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var productsResponse by mutableStateOf<ValueOrException<List<MProduct>>>(ValueOrException.Loading)
        private set

    var orderItemsList = mutableStateListOf<MOrderItem>()

    val customerId = savedStateHandle.get<String>(DestinationOneArg)
    val orderId = savedStateHandle.get<String>(DestinationTwoArg)

    init {
        launchCatching {
            if (orderStorageService.getOrder(orderId.toString()) is ValueOrException.Failure) {
                orderItemsResponse = ValueOrException.Success(orderItemsList)
                Log.d("log", "order id: $orderId")
            } else {
                try {
                    orderItemsResponse = ValueOrException.Loading
                    orderItemStorageService.getOrderItemsByOrderId(orderId!!).collect{ response->
                        orderItemsResponse = response
                    }
                } catch (e: java.lang.Exception){
                    orderItemsResponse = ValueOrException.Failure(e)
                }
                Log.d("log", "order id: $orderId")
            }
        }
        getProducts()
    }

    fun onSaveOrderItem(orderItem: MOrderItem){
        saveOrderItemResponse = ValueOrException.Loading
        launchCatching {
            saveOrderItemResponse = try {
                orderItemStorageService.addOrderItem(orderItem)
            } catch (e: Exception){
                ValueOrException.Failure(e)
            }
        }
    }

    fun onSaveOrderToFirebae(order: MOrder, onComplete:() -> Unit){
        saveOrderToFirebase = ValueOrException.Loading
        launchCatching {
            saveOrderToFirebase = try {
                orderStorageService.addOrder(order)
            } catch (e: Exception){
                ValueOrException.Failure(e)
            }
        }
        if (saveOrderItemResponse is ValueOrException.Success){
            onComplete()
        }
    }

    private fun getProducts() {
        launchCatching {
            productsResponse = ValueOrException.Loading
            productStorageService.getAllProducts().collect { response ->
                productsResponse = response
            }
        }
    }

}