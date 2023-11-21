package com.example.onlab.service.imp

import android.util.Log
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.model.MOrderItem
import com.example.onlab.model.MUser
import com.example.onlab.service.AuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthServiceImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val currentUser: Flow<MUser>
        get() = callbackFlow {

            val query = firestore.collection("users")
                .whereEqualTo("user_id", currentUserId)
                .limit(1) // Assuming there should be only one document with the given user_id

            val querySnapshot = query.get().await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val userId = documentSnapshot.getString("user_id") ?: ""
                val displayName = documentSnapshot.getString("display_name") ?: ""
                val address = documentSnapshot.getString("address") ?: ""
                val email = documentSnapshot.getString("email") ?: ""
                val image = documentSnapshot.getString("image") ?: ""

                val user = MUser(null, userId, displayName, address, email, image)
                if (user != null) {
                    this.trySend(user)
                }
            }
            close() // close the flow when done
        }




//    override val currentUser: Flow<MUser>
//        get() = callbackFlow {
//            val listener =
//                FirebaseAuth.AuthStateListener { auth ->
//                                        Log.d("log", " and $currentUserId: ")
//
//                    this.trySend(auth.currentUser?.let { MUser(userId = it.uid, displayName = it.displayName!!, address = it.email!!, id = it.uid  ) } ?: MUser())
//                }
//            auth.addAuthStateListener(listener)
//            awaitClose { auth.removeAuthStateListener(listener) }
//        }

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
                val user = MUser(userId = userId.toString(), email = email, displayName = "Not defined", address = "Not defined", id = null, image = "https://firebasestorage.googleapis.com/v0/b/orderapp-7d65f.appspot.com/o/images%2F1684754679358_image_cfbae3a8-86b8-4416-a0ac-c312b0305c75.jpg?alt=media&token=80b73308-3b08-4513-b91b-3d87960045b3").toMap()

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