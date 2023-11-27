package com.example.onlab.screen.order

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.R
import com.example.onlab.components.SnackbarManager
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Order
import com.example.onlab.model.OrderItem
import com.example.onlab.model.Product
import com.example.onlab.navigation.DestinationOneArg
import com.example.onlab.navigation.DestinationTwoArg
import com.example.onlab.repository.OrderItemsRepository
import com.example.onlab.service.OrderItemStorageService
import com.example.onlab.service.OrderStorageService
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import com.example.onlab.R.string as AppText

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderItemStorageService: OrderItemStorageService,
    private val orderStorageService: OrderStorageService,
    private val productStorageService: ProductStorageService,
    private val memoryOrderItemsRepository: OrderItemsRepository,
    savedStateHandle: SavedStateHandle
): OrderAppViewModel() {

    var orderItemsResponse by mutableStateOf<ValueOrException<List<OrderItem>>>(ValueOrException.Loading)
        private set

    var saveOrderItemResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var saveOrderToFirebase by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var productsResponse by mutableStateOf<ValueOrException<List<Product>>>(ValueOrException.Loading)
        private set

    var deleteOrderItemResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    private val _state = MutableStateFlow<ValueOrException<List<OrderItem>>>(ValueOrException.Loading)
    val state = _state.asStateFlow()

    val customerId = savedStateHandle.get<String>(DestinationOneArg)
    val orderId = savedStateHandle.get<String>(DestinationTwoArg)

    init {
        loadOrderItems()
        getProducts()
    }

    fun loadOrderItems(){
        launchCatching {
            try {
                _state.value = ValueOrException.Loading
                delay(1000)
                orderItemStorageService.getOrderItemsByOrderId(orderId!!).collect{ response->
                    orderItemsResponse = response
                    when (response) {
                        is ValueOrException.Success<List<OrderItem>> -> {
                            memoryOrderItemsRepository.getOrderItemsFromNetwork(response.data)
                            val list = memoryOrderItemsRepository.getOrderItems()
                            _state.value = ValueOrException.Success(list)
                        }
                        is ValueOrException.Failure -> SnackbarManager.displayMessage(AppText.data_loading_error)
                        else -> {}
                    }
                }
            } catch (e: Exception){
                _state.value = ValueOrException.Failure(e)
            }
        }
    }
    fun onSaveOrderItem(orderItem: OrderItem){
        saveOrderItemResponse = ValueOrException.Loading
        launchCatching {
            saveOrderItemResponse = try {
                orderItemStorageService.addOrderItem(orderItem)
            } catch (e: Exception){
                ValueOrException.Failure(e)
            }
        }
    }

    fun onSaveOrderToFirebae(order: Order, onComplete:() -> Unit){
        launchCatching {
            if(orderStorageService.getOrder(order.id.toString()) is ValueOrException.Failure){
                orderStorageService.addOrder(order)
            } else {

            }
        }

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
            when(productsResponse){
                is ValueOrException.Failure -> SnackbarManager.displayMessage(AppText.data_loading_error)
                else -> Unit
            }
        }
    }

    fun updateOrderItemLocally(orderItem: OrderItem){
        launchCatching {
            _state.value = ValueOrException.Loading
            delay(500)
            memoryOrderItemsRepository.updateOrderItem(orderItem)
            _state.value = ValueOrException.Success(memoryOrderItemsRepository.getOrderItems())
            SnackbarManager.displayMessage(R.string.save_success)
        }
    }

    fun onDeleteOrderItemFromDb(orderItem: OrderItem){
        launchCatching {
            deleteOrderItemResponse = ValueOrException.Loading
            //delay(1000)
            try {
                Log.d("log", "siker $orderItem")
                deleteOrderItemResponse = orderItemStorageService.deleteOrderItem(orderItem.id.toString())
                Log.d("log", "siker $deleteOrderItemResponse")

            } catch (e: Exception){
                Log.d("log", "onDeleteOrderItemFromDb: nem sikerult torolni $e")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSaveClick() {
        launchCatching {
            when (orderItemsResponse) {
                is ValueOrException.Success -> {
                    val orderResultInDb = orderStorageService.getOrder(orderId.toString())
                    if (orderResultInDb is ValueOrException.Failure) {
                        Log.d("log", "fail")
                        val newOrderId = UUID.randomUUID().toString()
                        val newOrder = Order(
                            orderId = newOrderId,
                            date = LocalDate.now().toString(),
                            customerID = customerId.toString(),
                            status = 0,
                            madeby = FirebaseAuth.getInstance().currentUser!!.email!!
                        )
                        orderStorageService.addOrder(newOrder)
                        memoryOrderItemsRepository.getOrderItems().forEach { orderItem ->
                            orderItem.orderID = newOrderId
                            orderItem.statusID = 3
                            orderItemStorageService.addOrderItem(orderItem)
                        }
                    } else {
                        val updatedItems = memoryOrderItemsRepository.getOrderItems().forEach{
                            Log.d("log", "onSaveClick: ${it.amount}")

                            if (it.statusID == 0) {
                                try {
                                    orderItemStorageService.updateOrderItem(it)
                                    Log.d("log", "onSaveClick: Status updated successfully")
                                } catch (e: Exception) {
                                    Log.e("log", "onSaveClick: Error updating status", e)
                                }
                            }
                            if (it.statusID == -1){
                                it.statusID = 3
                                orderItemStorageService.addOrderItem(it)
                            }
                            if (it.amount == 0){
                                orderItemStorageService.deleteOrderItem(it.id.toString())
                            }
                        }
                        Log.d("log", "onSaveClick: $updatedItems}")
                    }
                    SnackbarManager.displayMessage(R.string.save_success)
                }
                is ValueOrException.Failure -> SnackbarManager.displayMessage(R.string.delete_error)
                else -> Unit
            }
        }
    }
}