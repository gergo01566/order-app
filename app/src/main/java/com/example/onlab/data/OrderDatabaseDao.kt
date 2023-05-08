package com.example.onlab.data

import androidx.room.*
import com.example.onlab.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDatabaseDao {
    @Query("SELECT * FROM order_tbl")
    fun getAllOrder(): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(order: Order)

    @Query("DELETE FROM order_tbl")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteOrder(order: Order)
}