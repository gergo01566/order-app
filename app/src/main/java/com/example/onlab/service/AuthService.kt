package com.example.onlab.service

import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUserId: String
    val currentUser: Flow<com.example.onlab.model.User>

    suspend fun signInWithEmailAndPassowrd(email: String, password: String, onFailure: () -> Unit, onComplete: () -> Unit)
    suspend fun createUser(email: String, password: String, onFailure: () -> Unit, onComplete: () -> Unit)
    suspend fun resetPassword(email: String, onFailure: () -> Unit, onComplete: () -> Unit)
    suspend fun signOut()
    suspend fun deleteProfile()
}