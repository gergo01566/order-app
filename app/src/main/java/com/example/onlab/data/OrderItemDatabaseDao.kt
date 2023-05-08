package com.example.onlab.data

import androidx.room.*
import com.example.onlab.model.OrderItem
import kotlinx.coroutines.flow.Flow
import java.util.*

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

    @Query("SELECT product_tbl.product_title \n" +
            "FROM product_tbl \n" +
            "JOIN order_item_tbl ON product_tbl.id = order_item_tbl.productID \n" +
            "WHERE order_item_tbl.productID = :productID")
    fun getProductName(productID: UUID): Flow<String>
}