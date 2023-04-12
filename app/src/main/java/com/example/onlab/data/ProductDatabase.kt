package com.example.onlab.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.onlab.model.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDatabaseDao
}