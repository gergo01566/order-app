package com.example.onlab.data.customer

import androidx.room.*
import com.example.onlab.model.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDatabaseDao {
    @Query("SELECT * FROM customer_tbl")
    fun getAllCustomer(): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: Customer)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(customer: Customer)

    @Query("DELETE FROM product_tbl")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteProduct(customer: Customer)
}