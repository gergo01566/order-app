package com.example.onlab.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.onlab.data.customer.CustomerDatabaseDao
import com.example.onlab.model.*

@Database(entities = [Product::class, Customer::class, OrderItem::class, Order::class], version = 5, exportSchema = false)
@TypeConverters(LocalDateConverter::class)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDatabaseDao
    abstract fun customerDao(): CustomerDatabaseDao
    abstract fun orderItemDao(): OrderItemDatabaseDao

    abstract fun orderDao(): OrderDatabaseDao
}