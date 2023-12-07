package com.example.orderapp.model.service.imp

import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.User
import com.example.orderapp.model.service.UserStorageService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserStorageServiceImp
@Inject constructor(private val firestore: FirebaseFirestore, private val firebaseStorage: FirebaseStorage):
    UserStorageService {
    override suspend fun getUser(userId: String): ValueOrException<User> {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("user_id", userId)
                .get().await()
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val userId = documentSnapshot.getString("user_id") ?: ""
                val displayName = documentSnapshot.getString("display_name") ?: ""
                val address = documentSnapshot.getString("address") ?: ""
                val email = documentSnapshot.getString("email") ?: ""
                val image = documentSnapshot.getString("image") ?: ""

                val currentUser = User(null, userId, displayName, address, email, image)
                ValueOrException.Success(currentUser)
            } else {
                ValueOrException.Failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

    override suspend fun addUser(user: User): ValueOrException<Boolean> {
        return try {
            firestore.collection("users").add(user).addOnCompleteListener {
                ValueOrException.Success(true)
            }.addOnFailureListener{
                ValueOrException.Failure(e = it)
            }
            ValueOrException.Success(false)
        } catch (e: Exception){
            ValueOrException.Failure(e = e)
        }
    }

    override suspend fun updateUser(user: User): ValueOrException<Boolean> {
        return try {
            val userToUpdate = mapOf(
                "address" to user.address,
                "display_name" to user.displayName,
                "email" to user.email,
                "user_id" to user.userId
            )
            firestore.collection("users").document(user.userId).update(userToUpdate).await()
            if (!user.image.startsWith("https")) {
                firebaseStorage.uploadAndGetDownloadUrl("users", user.userId, "image", user.image)
            }
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): ValueOrException<Boolean> {
        try {
            // Step 1: Identify the document to delete
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("user_id", userId)
                .limit(1)
                .get()
                .await()

            return if (!querySnapshot.isEmpty) {
                val documentReference = querySnapshot.documents[0].reference
                documentReference.delete().await()
                ValueOrException.Success(true)
            } else {
                ValueOrException.Success(false)
            }
        } catch (e: Exception) {
            return ValueOrException.Failure(e = e)
        }
    }


}