package com.example.onlab.repository

import com.example.onlab.data.DataOrException
import com.example.onlab.model.MOrderItem
import com.example.onlab.model.MProduct
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderItemFireRepository @Inject constructor(
    private val queryOrderItem: Query
) {

    suspend fun getAllOrderItemsFromDatabase(): DataOrException<List<MOrderItem>, Boolean, Exception> {
        val dataOrException = DataOrException<List<MOrderItem>, Boolean, Exception>()

        try {
            dataOrException.loading = true
            dataOrException.data = queryOrderItem.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MOrderItem::class.java)!!
            }
            if(!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false

        } catch (exception: FirebaseFirestoreException){
            dataOrException.e = exception
        }
        return dataOrException
    }

}