package com.example.onlab.screen.product

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.components.SnackbarManager
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.OrderItem
import com.example.onlab.model.Product
import com.example.onlab.navigation.DestinationOneArg
import com.example.onlab.navigation.DestinationThreeArg
import com.example.onlab.navigation.DestinationTwoArg
import com.example.onlab.repository.OrderItemsRepository
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.onlab.R


@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val storageService: ProductStorageService,
    private val memoryOrderItemsRepository: OrderItemsRepository,
    savedStateHandle: SavedStateHandle
) : OrderAppViewModel() {

    private var searchText by mutableStateOf("")

    private var selectedCategory by mutableStateOf<Category?>(null)

    var productsResponse by mutableStateOf<ValueOrException<List<Product>>>(ValueOrException.Loading)
        private set

    var deleteProductResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    val isOrdering = !savedStateHandle.get<String>(DestinationTwoArg).isNullOrEmpty()

    val orderId = savedStateHandle.get<String>(DestinationOneArg).toString()

    val customerId = savedStateHandle.get<String>(DestinationThreeArg).toString()

    init {
        getProducts()
    }

    private fun getProducts() {
        launchCatching {
            productsResponse = ValueOrException.Loading
            storageService.getAllProducts().collect { response ->
                productsResponse = response
            }
        }
        when (productsResponse){
            is ValueOrException.Failure -> SnackbarManager.displayMessage(R.string.data_loading_error)
            else -> Unit
        }
    }

    fun onDeleteProduct(productId: String, onComplete: () -> Unit){
        deleteProductResponse = ValueOrException.Loading
        launchCatching {
            deleteProductResponse = storageService.deleteProduct(productId)
            when(deleteProductResponse){
                is ValueOrException.Failure -> SnackbarManager.displayMessage(R.string.delete_error)
                is ValueOrException.Success -> {
                    SnackbarManager.displayMessage(R.string.save_success)
                    onComplete()
                }
                else -> Unit
            }
        }
    }

    fun onSearchTextChanged(newText: String) {
        searchText = newText
        productsResponse = ValueOrException.Loading
        launchCatching {
            storageService.getProductsByCategoryAndText(searchText, selectedCategory).collect{ response ->
                productsResponse = response
            }
        }
    }

    fun onCategoryChanged(category: Category){
        selectedCategory = category
        productsResponse = ValueOrException.Loading
        launchCatching {
            storageService.getProductsByCategoryAndText(searchText, selectedCategory).collect{ response ->
                productsResponse = response
            }
        }
    }

    fun onAddOrderItemLocally(mOrderItem: OrderItem){
        launchCatching {
            memoryOrderItemsRepository.insertOrderItem(mOrderItem)
        }
    }
}
