package com.example.orderapp.screens.product

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import com.example.orderapp.common.snackbar.SnackbarManager
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.Category
import com.example.orderapp.model.OrderItem
import com.example.orderapp.model.Product
import com.example.orderapp.navigation.DestinationOneArg
import com.example.orderapp.navigation.DestinationThreeArg
import com.example.orderapp.navigation.DestinationTwoArg
import com.example.orderapp.model.repository.OrderItemsRepository
import com.example.orderapp.model.service.ProductStorageService
import com.example.orderapp.screens.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.orderapp.R


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
