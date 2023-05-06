package com.example.onlab.data

import androidx.room.*
import com.example.onlab.model.OrderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDatabaseDao {
    @Query("SELECT * FROM order_item_tbl")
    fun getAllOrderItem(): Flow<List<OrderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderItem: OrderItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(orderItem: OrderItem)

    @Query("DELETE FROM order_item_tbl")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItem)
}