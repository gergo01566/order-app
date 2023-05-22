package com.example.onlab.di

import android.content.Context
import androidx.room.Room
import com.example.onlab.data.OrderDatabaseDao
import com.example.onlab.data.OrderItemDatabaseDao
import com.example.onlab.data.ProductDatabase
import com.example.onlab.data.ProductDatabaseDao
import com.example.onlab.data.customer.CustomerDatabaseDao
import com.example.onlab.repository.FireRepository
import com.example.onlab.repository.OrderFireRepository
import com.example.onlab.repository.OrderItemFireRepository
import com.example.onlab.repository.ProductFireRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
//used to add bindings to hilt
object AppModule {

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
    fun provideFireOrderItemRepository()= OrderItemFireRepository(queryOrderItem = FirebaseFirestore.getInstance().collection("order_items"))

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
}