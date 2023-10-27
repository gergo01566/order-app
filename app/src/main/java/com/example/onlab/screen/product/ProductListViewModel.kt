package com.example.onlab.screen.product

import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.DataOrException
import com.example.onlab.model.Category
import com.example.onlab.model.MProduct
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val storageService: ProductStorageService
) : OrderAppViewModel() {

    val products = storageService.products
    var searchText by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf<Category?>(null)
        private set


    val searchResults: Flow<List<MProduct>> =
        combine(
            snapshotFlow { searchText },
            snapshotFlow { selectedCategory }
        ) { searchQuery, category ->
            Pair(searchQuery, category)
        }.flatMapLatest { (searchQuery, category) ->
            products.map { productsList ->
                productsList.filter { product ->
                    val matchesSearch = searchQuery.isEmpty() || product.doesMatchSearchQuery(searchQuery)
                    val matchesCategory = category == null || category == Category.Összes || product.category == category.toString()
                    matchesSearch && matchesCategory
                }
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000)
        )



    fun onDeleteProduct(productId: String){
        launchCatching {
            storageService.deleteProduct(productId)
        }
    }

    fun onSearchTextChanged(newText: String) {
        searchText = newText
    }

    fun onCategoryChanged(category: Category){
        selectedCategory = category
    }

    val data: MutableState<DataOrException<Flow<List<MProduct>>, Boolean,java.lang.Exception>> = mutableStateOf(
        DataOrException(products, true, Exception(""))
    )
}
