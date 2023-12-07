package com.example.onlab.di

import com.example.onlab.model.service.AuthService
import com.example.onlab.model.service.ProductStorageService
import com.example.onlab.model.service.imp.AuthServiceImp
import com.example.onlab.model.service.imp.ProductStorageServiceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceModule {

    @Binds
    abstract fun bindAuthService(authService: AuthServiceImp): AuthService

    @Binds abstract fun provideProductStorageService(impl: ProductStorageServiceImp): ProductStorageService
}