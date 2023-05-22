package com.example.onlab.repository

import com.example.onlab.data.DataOrException
import com.example.onlab.model.MOrder
import com.example.onlab.model.MOrderItem
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderFireRepository @Inject constructor(
    private val queryOrder: Query
) {

    suspend fun getAllOrders(): DataOrException<List<MOrder>, Boolean, Exception> {
        val dataOrException = DataOrException<List<MOrder>, Boolean, Exception>()

        try {
            dataOrException.loading = true
            dataOrException.data = queryOrder.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MOrder::class.java)!!
            }
            if(!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false

        } catch (exception: FirebaseFirestoreException){
            dataOrException.e = exception
        }
        return dataOrException
    }

}