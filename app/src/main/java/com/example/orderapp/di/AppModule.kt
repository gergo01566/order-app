package com.example.orderapp.di

import com.example.orderapp.model.service.*
import com.example.orderapp.model.service.imp.*
import com.example.orderapp.model.repository.MemoryOrderItemRepositoryImp
import com.example.orderapp.model.repository.OrderItemsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {

    @Binds
    abstract fun bindAuthService(authService: AuthServiceImp): AuthService

    @Binds
    abstract fun bindProductStorageService(imp: ProductStorageServiceImp): ProductStorageService

    @Binds
    abstract fun bindCustomerStorageService(imp: CustomerStorageServiceImp): CustomerStorageService

    @Binds
    abstract fun bindOrderStorageService(imp: OrderStorageServiceImp): OrderStorageService

    @Binds
    abstract fun bindOrderItemStorageService(imp: OrderItemStorageServiceImp): OrderItemStorageService

    @Binds
    abstract fun bindProductRepository(imp: MemoryOrderItemRepositoryImp): OrderItemsRepository

    @Binds
    abstract fun bindUserStorageService(imp: UserStorageServiceImp): UserStorageService

}