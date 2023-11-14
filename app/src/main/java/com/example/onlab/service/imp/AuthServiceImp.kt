package com.example.onlab.service.imp

import com.example.onlab.model.MUser
import com.example.onlab.service.AuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthServiceImp @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val currentUser: Flow<MUser>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { MUser(userId = it.uid, displayName = it.displayName!!, address = it.email!!, id = it.uid  ) } ?: MUser())
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun signInWithEmailAndPassowrd(email: String, password: String, onFailure: () -> Unit, onComplete: () -> Unit){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete()
            } else {
                onFailure()
            }
        }
    }

    override suspend fun createUser(email: String, password: String, onFailure: () -> Unit, onComplete: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                val user = MUser(userId = userId.toString(), displayName = "Anonymous", address = "Budapest", id = null).toMap()

                FirebaseFirestore.getInstance().collection("users")
                    .add(user).addOnCompleteListener {
                        if (task.isSuccessful) {
                            onComplete()
                        } else {
                            onFailure()
                        }
                    }
            }
        }
    }

    override suspend fun resetPassword(email: String, onFailure: () -> Unit, onComplete: () -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete()
            } else {
                onFailure()
            }
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun deleteProfile() {
        auth.currentUser?.delete()
    }

}