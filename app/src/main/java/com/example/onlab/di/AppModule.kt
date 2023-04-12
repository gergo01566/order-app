package com.example.onlab.di

import android.content.Context
import androidx.room.Room
import com.example.onlab.data.ProductDatabase
import com.example.onlab.data.ProductDatabaseDao
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
    fun provideProductDao(productDatabase: ProductDatabase): ProductDatabaseDao = productDatabase.productDao()

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