package com.example.onlab.service.imp

import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


open class FirebaseStorage @Inject constructor(private val storage: FirebaseStorage, private val firestore: FirebaseFirestore){
    suspend fun uploadAndGetDownloadUrl(collectionName: String, id: String, fieldName: String, image: String) {

        val fileName =
            "${System.currentTimeMillis()}_${image.toUri().lastPathSegment}"
        val imageRef = storage.reference.child("images/${fileName}")

        val uploadTask: UploadTask = imageRef.putFile(image.toUri())

        uploadTask.addOnSuccessListener { taskSnapshot ->
            storage.reference.child("images/${fileName}").downloadUrl.addOnSuccessListener { downloadUrl ->
                firestore.collection(collectionName).document(id).update(hashMapOf(fieldName to downloadUrl.toString()) as Map<String, Any>)
            }
        }.await()
    }
}