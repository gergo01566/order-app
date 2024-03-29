package com.example.orderapp.model.service

import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerStorageService {
    var searchQuery: String

    fun getAllCustomers(): Flow<ValueOrException<List<Customer>>>

    fun getCustomersByText(text: String?): Flow<ValueOrException<List<Customer>>>

    suspend fun getCustomer(customerId: String): ValueOrException<Customer>

    suspend fun addCustomer(customer: Customer): ValueOrException<Boolean>

    suspend fun updateCustomer(customer: Customer): ValueOrException<Boolean>

    suspend fun deleteCustomer(customerId: String): ValueOrException<Boolean>
}