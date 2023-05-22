package com.example.onlab.repository

import android.net.Uri
import com.example.onlab.data.DataOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.model.MProduct
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductFireRepository @Inject constructor(
    private val queryProduct: Query
) {

    suspend fun getAllProductsFromDatabase(): DataOrException<List<MProduct>, Boolean, Exception> {
        val dataOrException = DataOrException<List<MProduct>, Boolean, Exception>()

        try {
            dataOrException.loading = true
            dataOrException.data = queryProduct.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MProduct::class.java)!!
            }
            if(!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false

        } catch (exception: FirebaseFirestoreException){
            dataOrException.e = exception
        }
        return dataOrException
    }

}