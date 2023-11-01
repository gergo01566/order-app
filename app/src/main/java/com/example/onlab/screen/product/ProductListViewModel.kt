package com.example.onlab.screen.product

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.DataOrException
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.LoadingState
import com.example.onlab.model.MOrder
import com.example.onlab.model.MProduct
import com.example.onlab.navigation.DestinationProductDetails
import com.example.onlab.navigation.DestinationProductList
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val storageService: ProductStorageService
) : OrderAppViewModel() {

    val products: MutableStateFlow<List<MProduct>> = MutableStateFlow(emptyList())


    var searchText by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf<Category?>(null)
        private set

    var productsResponse by mutableStateOf<ValueOrException<List<MProduct>>>(ValueOrException.Loading)
        private set

    init {
        getProducts()
    }

    val searchTextFlow = snapshotFlow { searchText }

//    val searchResults: Flow<List<MProduct>> = combine(
//        productsResponse,
//        snapshotFlow { selectedCategory },
//        searchTextFlow
//    ) { response, category, searchQuery ->
//        Triple(response, category, searchQuery)
//    }.flatMapLatest { (response, category, searchQuery) ->
//        when (response) {
//            is ValueOrException.Loading -> flowOf(emptyList())
//            is ValueOrException.Success -> {
//                flow {
//                    val filteredProducts = response.value.filter { product ->
//                        val matchesSearch = searchQuery.isEmpty() || product.doesMatchSearchQuery(searchQuery)
//                        val matchesCategory = category == null || category == Category.Összes || product.category == category.toString()
//                        matchesSearch && matchesCategory
//                    }
//                    emit(filteredProducts)
//                }
//            }
//            is ValueOrException.Failure -> flowOf(emptyList())
//        }
//    }


    private fun getProducts() {
        viewModelScope.launch {
            storageService.getAllProducts().collect { response ->
                productsResponse = response
            }
        }
    }

    fun onDeleteProduct(productId: String, onComplete: () -> Unit){
        launchCatching {
            try {
                storageService.deleteProduct(productId){
                    onComplete()
                }
            } catch (e: Exception){
            }
        }
    }

    fun onSearchTextChanged(newText: String) {
        searchText = newText
        launchCatching {
            storageService.getProductsByCategoryAndText(searchText, selectedCategory).collect{ response ->
                productsResponse = response as ValueOrException<List<MProduct>>
            }
        }


    }

    fun onCategoryChanged(category: Category){
        selectedCategory = category
        launchCatching {
            storageService.getProductsByCategoryAndText(searchText, selectedCategory).collect{ response ->
                productsResponse = response
            }
        }
        Log.d("CATEGORY", "onCategoryChanged: $selectedCategory")
    }

//    val searchResults: Flow<List<MProduct>> =
//        combine(
//            snapshotFlow { searchText },
//            snapshotFlow { selectedCategory }
//        ) { searchQuery, category ->
//            Pair(searchQuery, category)
//        }.flatMapLatest { (searchQuery, category) ->
//            products.map { productsList ->
//                productsList.filter { product ->
//                    val matchesSearch = searchQuery.isEmpty() || product.doesMatchSearchQuery(searchQuery)
//                    val matchesCategory = category == null || category == Category.Összes || product.category == category.toString()
//                    matchesSearch && matchesCategory
//                }
//            }
//
//        }.stateIn(
//            scope = viewModelScope,
//            initialValue = emptyList(),
//            started = SharingStarted.WhileSubscribed(5_000)
//        )
}
