package com.example.onlab.screen.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.onlab.model.Product
import com.example.onlab.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepository) : ViewModel() {

    val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    val _isSearching = MutableStateFlow(false)

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList = searchText
        .debounce(300L)
        .onEach { _isSearching.update { true } }
        .combine(_productList) { text, products ->
            if(text.isBlank()) {
                products
            } else {
                products.filter { product->
                    product.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _productList.value )

    init {
        //making sure our highway has a lot of different lanes
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllProducts().distinctUntilChanged().
                collect{ listOfProducts ->
                    if (listOfProducts.isNullOrEmpty()){
                        Log.d("Empty", "Empty list")
                    } else {
                        _productList.value = listOfProducts
                    }
            }
        }
    }
    fun addProduct(product: Product) = viewModelScope.launch { repository.addProduct(product) }
    fun updateProduct(product: Product) = viewModelScope.launch { repository.updateProduct(product) }
    fun removeProduct(product: Product) = viewModelScope.launch { repository.deleteProduct(product) }
    fun getProductsByCategory(category: String): List<Product> {
        return productList.value.filter {
            it.category.toString() == category
        }
    }

    fun getProductById(id: String): Product? {
        return productList.value.find {
            it.id.toString() == id
        }
    }

    fun onSearchTextChange(text: String){
        _searchText.value = text
    }

}