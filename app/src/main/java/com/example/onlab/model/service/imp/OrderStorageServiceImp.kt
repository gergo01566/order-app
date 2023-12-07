package com.example.onlab.model.service.imp

import androidx.compose.runtime.MutableState
import com.example.onlab.model.ValueOrException
import com.example.onlab.model.Order
import com.example.onlab.model.service.OrderStorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderStorageServiceImp
@Inject
constructor(private val firestore: FirebaseFirestore) :
    OrderStorageService {

    override fun getOrdersByStatus(status: MutableState<Boolean>) = callbackFlow {
        val query = firestore.collection("orders")

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                if (e.equals(FirebaseFirestoreException.Code.UNAVAILABLE)){
                    ValueOrException.Failure(e)
                }
            }
            val response = if (snapshot != null && !snapshot.isEmpty) {
                val orders = snapshot.toObjects(Order::class.java)
                    .filter { order ->
                        order.isCompleted == status.value
                    }
                ValueOrException.Success(orders)
            } else {
                ValueOrException.Failure(e)
            }
            trySend(response)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getOrder(orderId: String): ValueOrException<Order> {
        return try {
            val querySnapshot = firestore.collection("orders")
                .whereEqualTo("order_id", orderId)
                .get().await()
            if (!querySnapshot.isEmpty) {
                val order = querySnapshot.documents[0].toObject<Order>()
                ValueOrException.Success(order!!)
            } else {
                ValueOrException.Failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

    override suspend fun addOrder(order: Order): ValueOrException<Boolean> {
        return try {
            firestore.collection("orders").add(order).addOnSuccessListener { documentRef ->
                firestore.collection("orders").document(documentRef.id).update(hashMapOf(
                    "id" to documentRef.id) as Map<String, Any>).addOnCompleteListener {
                        ValueOrException.Success(true)
                }
            }.addOnFailureListener{
                ValueOrException.Failure(e = it)
            }
            ValueOrException.Success(false)
        } catch (e: Exception){
            ValueOrException.Failure(e = e)
        }
    }

    override suspend fun deleteOrder(
        orderId: String,
    ): ValueOrException<Boolean> {
        return try {
            firestore.collection("orders").document(orderId).delete().await()
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

    override suspend fun updateOrder(order: Order): ValueOrException<Boolean> {
        return try {
            val orderToUpdate = mapOf(
                "is_completed" to !order.isCompleted
            )
            FirebaseFirestore.getInstance().collection("orders").document(order.id!!).update(orderToUpdate).await()
            ValueOrException.Success(true)
        } catch (e: Exception){
            ValueOrException.Failure(e)
        }
    }


}