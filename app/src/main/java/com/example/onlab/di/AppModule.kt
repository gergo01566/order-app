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
import com.example.onlab.service.AuthService
import com.example.onlab.service.AuthServiceImp
import com.example.onlab.service.ProductStorageService
import com.example.onlab.service.ProductStorageServiceImp
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
abstract class AppModule {

    @Binds
    abstract fun bindAuthService(authService: AuthServiceImp): AuthService

    @Binds
    abstract fun bindProductStorageService(imp: ProductStorageServiceImp): ProductStorageService

}