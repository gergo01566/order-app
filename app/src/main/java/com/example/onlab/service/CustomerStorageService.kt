package com.example.onlab.service

import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MCustomer
import kotlinx.coroutines.flow.Flow

interface CustomerStorageService {
    var searchQuery: String

    fun getAllCustomers(): Flow<ValueOrException<List<MCustomer>>>

    fun getCustomersByText(text: String?): Flow<ValueOrException<List<MCustomer>>>

    suspend fun getCustomer(customerId: String): ValueOrException<MCustomer>

    suspend fun addCustomer(customer: MCustomer): ValueOrException<Boolean>

    suspend fun updateCustomer(customer: MCustomer): ValueOrException<Boolean>

    suspend fun deleteCustomer(customerId: String): ValueOrException<Boolean>
}