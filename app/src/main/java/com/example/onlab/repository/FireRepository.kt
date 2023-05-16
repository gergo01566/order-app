package com.example.onlab.repository

import com.example.onlab.data.DataOrException
import com.example.onlab.model.MCustomer
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
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
}
