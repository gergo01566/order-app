package com.example.onlab.repository

import android.net.Uri
import com.example.onlab.data.DataOrException
import com.example.onlab.model.MCustomer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireRepository @Inject constructor(
    private val queryCustomer: Query
) {
    suspend fun getAllCustomersFromDatabase(): DataOrException<List<MCustomer>, Boolean, Exception> {
        val dataOrException = DataOrException<List<MCustomer>, Boolean, Exception>()

        try {
            dataOrException.loading = true
            dataOrException.data = queryCustomer.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MCustomer::class.java)!!
            }
            if(!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false

        } catch (exception: FirebaseFirestoreException){
            dataOrException.e = exception
        }
        return dataOrException
    }

    suspend fun addImageToFirebaseStorage(uri: Uri): String{
        val storageReference = FirebaseStorage.getInstance().reference
        val firestore = FirebaseFirestore.getInstance()
        val imagesCollection = firestore.collection("images")

        val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
        val imageRef = storageReference.child("images/${fileName}")

        val uploadTask = imageRef.putFile(uri)
        val uploadResult = uploadTask.await()

        val downloadUrl = imageRef.downloadUrl.await().toString()
        val imageDoc = hashMapOf("url" to downloadUrl)

        imagesCollection.document(fileName).set(imageDoc)
        return downloadUrl
    }
}
