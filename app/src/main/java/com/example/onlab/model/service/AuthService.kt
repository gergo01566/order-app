package com.example.onlab.model.service

import com.example.onlab.model.ValueOrException
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUserId: String
    val currentUser: Flow<com.example.onlab.model.User>

    suspend fun signInWithEmailAndPassowrd(email: String, password: String): ValueOrException<Boolean>
    suspend fun createUser(email: String, password: String): ValueOrException<Boolean>
    suspend fun resetPassword(email: String): ValueOrException<Boolean>
    suspend fun signOut(): ValueOrException<Boolean>
    suspend fun deleteProfile(): ValueOrException<Boolean>
}