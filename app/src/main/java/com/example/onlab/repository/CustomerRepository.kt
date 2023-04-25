package com.example.onlab.repository

import com.example.onlab.data.customer.CustomerDatabaseDao
import com.example.onlab.model.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CustomerRepository @Inject constructor(private val customerDatabaseDao: CustomerDatabaseDao){
    suspend fun addCustomer(customer: Customer) = customerDatabaseDao.insert(customer)
    suspend fun updateCustomer(customer: Customer) = customerDatabaseDao.update(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDatabaseDao.deleteProduct(customer)
    suspend fun deleteAllCustomer() = customerDatabaseDao.deleteAll()
    fun getAllCustomer(): Flow<List<Customer>> = customerDatabaseDao.getAllCustomer().flowOn(
        Dispatchers.IO).conflate()

}