package com.example.onlab.data

import androidx.room.*
import com.example.onlab.model.Category
import com.example.onlab.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDatabaseDao {

    @Query("SELECT * FROM product_tbl")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(product: Product)

    @Query("DELETE FROM product_tbl")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteProduct(product: Product)
}
