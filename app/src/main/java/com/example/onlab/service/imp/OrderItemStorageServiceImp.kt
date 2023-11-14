package com.example.onlab.service.imp

import android.util.Log
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MOrderItem
import com.example.onlab.service.OrderItemStorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderItemStorageServiceImp
@Inject
constructor(private val firestore: FirebaseFirestore):
    OrderItemStorageService{

    override fun getOrderItemsByOrderId(orderId: String) = callbackFlow {
        val query = firestore.collection("order_items")

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                if (e.equals(FirebaseFirestoreException.Code.UNAVAILABLE)){
                    ValueOrException.Failure(e)
                }
            }
            val response = if (snapshot != null && !snapshot.isEmpty) {
                val orderItems = snapshot.toObjects(MOrderItem::class.java)
                    .filter { orderItem ->
                        orderItem.orderID == orderId
                    }
                ValueOrException.Success(orderItems)
            } else {
                ValueOrException.Failure(e)
            }
            trySend(response)
        }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun addOrderItem(
        orderItem: MOrderItem,
    ): ValueOrException<Boolean> {
        return try {
            firestore.collection("order_items").add(orderItem).addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                firestore.collection("order_items").document(docId)
                    .update(
                        hashMapOf(
                            "id" to docId,
                        ) as Map<String, Any>
                    )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            ValueOrException.Success(true)
                        }
                    }
                    .addOnFailureListener {
                        ValueOrException.Failure(it)
                    }
            }
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e = e)
        }
    }

    override suspend fun deleteOrderItem(
        orderItemId: String,
    ): ValueOrException<Boolean> {
        Log.d("log", "deleteOrderItem: $")

        return try {
            firestore.collection("order_items").document(orderItemId).delete()
            ValueOrException.Success(true)
        } catch (e: Exception) {
            Log.d("log", "deleteOrderItem: $e")
            ValueOrException.Failure(e)
        }
    }

    override suspend fun updateOrderItem(
        orderItem: MOrderItem,
    ): ValueOrException<Boolean> {
        return try {
            val orderItemToUpdate = hashMapOf(
                "id" to orderItem.id,
                "item_amount" to orderItem.amount,
                "product_id" to orderItem.productID,
                "is_karton" to orderItem.carton,
                "order_id" to orderItem.orderID,
                "is_piece" to orderItem.piece,
                "status_id" to 3
            ).toMap()
            Log.d("log", "updateOrderItem: Updating order item with ID ${orderItem.id} to status 3")
            Log.d("log", "updateOrderItem: OrderItemToUpdate: $orderItemToUpdate")

            firestore.collection("order_items").document(orderItem.id!!).update(orderItemToUpdate)

            Log.d("log", "updateOrderItem: Order item updated successfully")

            ValueOrException.Success(true)
        } catch (e: Exception) {
            Log.d("log", "updateOrderItem: exception $e")
            ValueOrException.Failure(e)
        }
    }


    fun orderItemExist(orderItemId: String): Boolean {
        var exist = false
        firestore.collection("order_items").document(orderItemId).get().addOnSuccessListener {
            exist = it.exists()
        }
        return exist
    }
}