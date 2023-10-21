package com.example.onlab.service

import com.example.onlab.model.MUser
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUserId: String
    val currentUser: Flow<MUser>

    suspend fun signInWithEmailAndPassowrd(email: String, password: String)
    suspend fun createUser(email: String, password: String)
    suspend fun resetPassword(email: String)
    suspend fun createUserWithEmailAndPassword(email: String, password: String)

}