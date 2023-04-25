package com.example.onlab.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.onlab.data.customer.CustomerDatabaseDao
import com.example.onlab.model.Customer
import com.example.onlab.model.Product

@Database(entities = [Product::class, Customer::class], version = 2, exportSchema = false)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDatabaseDao
    abstract fun customerDao(): CustomerDatabaseDao
}