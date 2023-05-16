package com.example.onlab.viewModels

import android.provider.ContactsContract.Data
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onlab.data.DataOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MCustomerViewModel @Inject constructor(
    private val repository: FireRepository
) : ViewModel() {
    val data: MutableState<DataOrException<List<MCustomer>, Boolean, java.lang.Exception>> = mutableStateOf(
        DataOrException(listOf(), true, Exception(""))
    )

    init {
        getAllCustomersFromDatabase()
    }

    private fun getAllCustomersFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllCustomersFromDatabase()
            if(!data.value.data.isNullOrEmpty()) data.value.loading = false
        }
        Log.d("FB", "getAllCustomersFromDatabase: ${data.value.data?.toList().toString()}")
    }
}