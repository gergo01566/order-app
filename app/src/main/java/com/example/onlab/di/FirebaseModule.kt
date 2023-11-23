package com.example.onlab.di

import android.content.Context
import androidx.room.Room
import com.example.onlab.data.OrderDatabaseDao
import com.example.onlab.data.OrderItemDatabaseDao
import com.example.onlab.data.ProductDatabase
import com.example.onlab.data.ProductDatabaseDao
import com.example.onlab.data.customer.CustomerDatabaseDao
import com.example.onlab.repository.*
import com.example.onlab.service.OrderItemStorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
//used to add bindings to hilt
object FirebaseModule {

    @Provides fun auth(): FirebaseAuth = Firebase.auth

    @Provides fun firestore(): FirebaseFirestore = Firebase.firestore

    @Provides fun firestorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideFireCustomerRepository()= FireRepository(queryCustomer = FirebaseFirestore.getInstance().collection("customers"))

    @Singleton
    @Provides
    fun provideFireProductRepository()= ProductFireRepository(queryProduct = FirebaseFirestore.getInstance().collection("products"))

    @Singleton
    @Provides
    fun provideFireOrderRepository()= OrderFireRepository(queryOrder = FirebaseFirestore.getInstance().collection("orders"))

    @Singleton
    @Provides
    fun provideProductDao(productDatabase: ProductDatabase): ProductDatabaseDao = productDatabase.productDao()

    @Singleton
    @Provides
    fun provideCustomerDao(customerDatabase: ProductDatabase): CustomerDatabaseDao = customerDatabase.customerDao()

    @Singleton
    @Provides
    fun provideOrderItemDao(customerDatabase: ProductDatabase): OrderItemDatabaseDao = customerDatabase.orderItemDao()

    @Singleton
    @Provides
    fun provideOrderDao(orderDatabase: ProductDatabase): OrderDatabaseDao = orderDatabase.orderDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): ProductDatabase
        = Room.databaseBuilder(
            context,
            ProductDatabase::class.java,
            "onlab_db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideMemoryOrderItemRepository() = MemoryOrderItemRepository()
}