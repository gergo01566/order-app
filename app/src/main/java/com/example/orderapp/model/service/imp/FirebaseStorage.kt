package com.example.orderapp.model.service.imp

import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


open class FirebaseStorage @Inject constructor(private val storage: FirebaseStorage, private val firestore: FirebaseFirestore){
    suspend fun uploadAndGetDownloadUrl(collectionName: String, id: String, fieldName: String, image: String) {
        val fileName = "${System.currentTimeMillis()}_${image.toUri().lastPathSegment}"
        val imageRef = storage.reference.child("images/$fileName")
        imageRef.putFile(image.toUri()).await()
        val downloadUrl = imageRef.downloadUrl.await()
        firestore.collection(collectionName).document(id)
            .update(mapOf(fieldName to downloadUrl.toString())).await()
    }
}