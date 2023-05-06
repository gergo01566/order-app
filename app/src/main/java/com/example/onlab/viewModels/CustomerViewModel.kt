package com.example.onlab.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.model.Customer
import com.example.onlab.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(private val repository: CustomerRepository): ViewModel(){

    val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _customerList = MutableStateFlow<List<Customer>>(emptyList())
    val customerList = searchText
        .debounce(300L)
        .onEach { _isSearching.update { true } }
        .combine(_customerList) { text, customers ->
            if(text.isBlank()) {
                customers
            } else {
                customers.filter {customer ->
                    customer.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _customerList.value )
    //private var productList = mutableListOf<Product>()

    init {
        //making sure our highway has a lot of different lanes
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllCustomer().distinctUntilChanged().
            collect{ listOfCustomers ->
                if (listOfCustomers.isNullOrEmpty()){
                    Log.d("Empty", "Empty list")
                } else {
                    _customerList.value = listOfCustomers
                }
            }
        }
    }

    fun addCustomer(customer: Customer) = viewModelScope.launch { repository.addCustomer(customer) }
    fun updateCustomer(customer: Customer) = viewModelScope.launch { repository.updateCustomer(customer) }
    fun removeCustomer(customer: Customer) = viewModelScope.launch { repository.deleteCustomer(customer) }

    fun getCustomerById(id: String): Customer? {
        return customerList.value.find {
            it.id.toString() == id
        }
    }

    fun onSearchTextChange(text: String){
        _searchText.value = text
    }
}