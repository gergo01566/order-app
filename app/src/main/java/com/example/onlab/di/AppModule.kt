package com.example.onlab.di

import com.example.onlab.service.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
//used to add bindings to hilt
abstract class AppModule {

    @Binds
    abstract fun bindAuthService(authService: AuthServiceImp): AuthService

    @Binds
    abstract fun bindProductStorageService(imp: ProductStorageServiceImp): ProductStorageService

    @Binds
    abstract fun bindCustomerStorageService(imp: CustomerStorageServiceImp): CustomerStorageService

}