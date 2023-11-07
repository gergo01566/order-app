package com.example.onlab.screen.product

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.MOrderItem
import com.example.onlab.model.MProduct
import com.example.onlab.navigation.DestinationOneArg
import com.example.onlab.navigation.DestinationThreeArg
import com.example.onlab.navigation.DestinationTwoArg
import com.example.onlab.screen.order.OrderDetailsViewModel
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val storageService: ProductStorageService,
    savedStateHandle: SavedStateHandle
) : OrderAppViewModel() {

    private var searchText by mutableStateOf("")

    private var selectedCategory by mutableStateOf<Category?>(null)

    var productsResponse by mutableStateOf<ValueOrException<List<MProduct>>>(ValueOrException.Loading)
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
    }

    fun onDeleteProduct(productId: String, onComplete: () -> Unit){
        deleteProductResponse = ValueOrException.Loading
        launchCatching {
            try {
                deleteProductResponse = storageService.deleteProduct(productId)
                onComplete()
            } catch (_: Exception){

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
}
