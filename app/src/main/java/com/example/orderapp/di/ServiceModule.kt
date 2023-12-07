package com.example.orderapp.di

import com.example.orderapp.model.service.AuthService
import com.example.orderapp.model.service.ProductStorageService
import com.example.orderapp.model.service.imp.AuthServiceImp
import com.example.orderapp.model.service.imp.ProductStorageServiceImp
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