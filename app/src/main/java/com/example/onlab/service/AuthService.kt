package com.example.onlab.service

import com.example.onlab.model.MUser
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUserId: String
    val currentUser: Flow<MUser>

    suspend fun signInWithEmailAndPassowrd(email: String, password: String, onFailure: () -> Unit, onComplete: () -> Unit)
    suspend fun createUser(email: String, password: String, onFailure: () -> Unit, onComplete: () -> Unit)
    suspend fun resetPassword(email: String, onFailure: () -> Unit, onComplete: () -> Unit)
    suspend fun signOut()
    suspend fun deleteProfile()
}