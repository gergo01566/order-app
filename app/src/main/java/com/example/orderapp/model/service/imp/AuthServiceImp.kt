package com.example.orderapp.model.service.imp

import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.User
import com.example.orderapp.model.service.AuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthServiceImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val currentUser: Flow<User>
        get() = callbackFlow {

            val query = firestore.collection("users")
                .whereEqualTo("user_id", currentUserId)
                .limit(1) // Assuming there should be only one document with the given user_id

            val querySnapshot = query.get().await()

            var currentUser = User(
                "user123",
                "John Doe",
                "123 Main Street",
                "john.doe@example.com",
                "https://example.com/image.jpg"
            )

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val userId = documentSnapshot.getString("user_id") ?: ""
                val displayName = documentSnapshot.getString("display_name") ?: ""
                val address = documentSnapshot.getString("address") ?: ""
                val email = documentSnapshot.getString("email") ?: ""
                val image = documentSnapshot.getString("image") ?: ""

                currentUser = User(null, userId, displayName, address, email, image)
                this.trySend(currentUser)
            }

            val listener = FirebaseAuth.AuthStateListener {
                auth.currentUser?.let { currentUser }?.let { it1 -> this.trySend(it1) }
            }

            awaitClose{ Firebase.auth.removeAuthStateListener { listener } }

            close()
        }

    override suspend fun signInWithEmailAndPassowrd(email: String, password: String): ValueOrException<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

    override suspend fun createUser(email: String, password: String): ValueOrException<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            ValueOrException.Success(true)
        } catch (e: Exception){
            ValueOrException.Failure(e)
        }
    }

    override suspend fun resetPassword(email: String): ValueOrException<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            ValueOrException.Success(true)
        } catch (e: Exception){
            ValueOrException.Failure(e)
        }
    }

    override suspend fun signOut(): ValueOrException<Boolean> {
        return try {
            auth.signOut()
            ValueOrException.Success(true)
        } catch (e: Exception){
            ValueOrException.Failure(e)
        }
    }

    override suspend fun deleteProfile(): ValueOrException<Boolean> {
        return try {
            auth.currentUser?.delete()
            ValueOrException.Success(true)
        } catch (e: Exception){
            ValueOrException.Failure(e)
        }
    }

}