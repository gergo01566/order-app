package com.example.orderapp.model.service

import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.User

interface UserStorageService {
    suspend fun getUser(userId: String): ValueOrException<User>
    suspend fun addUser(user: User): ValueOrException<Boolean>
    suspend fun updateUser(user: User): ValueOrException<Boolean>
    suspend fun deleteUser(userId: String): ValueOrException<Boolean>
}