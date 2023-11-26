package com.example.onlab.service.imp

import androidx.core.net.toUri
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.User
import com.example.onlab.service.UserStorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserStorageServiceImp
@Inject constructor(private val firestore: FirebaseFirestore, private val storage: FirebaseStorage): UserStorageService{
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
                ValueOrException.Success(currentUser!!)
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
        try {
            val userToUpdate = mapOf(
                "address" to user.address,
                "display_name" to user.displayName,
                "email" to user.email,
                "user_id" to user.userId,
                "image" to if (user.image.startsWith("https")) {
                    user.image.toUri()
                } else {
                    //TODO

                    val fileName =
                        "${System.currentTimeMillis()}_${user.image.toUri().lastPathSegment}"
                    val imageRef = storage.reference.child("images/${fileName}")

                    val uploadTask: UploadTask = imageRef.putFile(user.image.toUri())

                    var downloadUrl = ""

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        storage.reference.child("images/${fileName}").downloadUrl.addOnSuccessListener { uri ->
                            downloadUrl = uri.toString()
                            firestore.collection("users").document(user.userId).update(hashMapOf("image" to downloadUrl) as Map<String, Any>)
                        }
                    }
                }
            )
            firestore.collection("users").document(user.userId).update(userToUpdate).await()
            return ValueOrException.Success(true)
        } catch (e: Exception) {
            return ValueOrException.Failure(e)
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