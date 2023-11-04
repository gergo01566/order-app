package com.example.onlab.screen.product

import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.MProduct
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val storageService: ProductStorageService
) : OrderAppViewModel() {

    private var searchText by mutableStateOf("")

    private var selectedCategory by mutableStateOf<Category?>(null)

    var productsResponse by mutableStateOf<ValueOrException<List<MProduct>>>(ValueOrException.Loading)
        private set

    var deleteProductResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
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
