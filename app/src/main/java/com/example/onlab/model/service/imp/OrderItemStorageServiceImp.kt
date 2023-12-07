package com.example.onlab.model.service.imp

import com.example.onlab.model.ValueOrException
import com.example.onlab.model.OrderItem
import com.example.onlab.model.service.OrderItemStorageService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class OrderItemStorageServiceImp
@Inject
constructor(private val firestore: FirebaseFirestore):
    OrderItemStorageService {

    override fun getOrderItemsByOrderId(orderId: String) = callbackFlow {
        val query = firestore.collection("order_items").whereEqualTo("order_id", orderId)

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null && !snapshot.isEmpty) {
                val orderItems = snapshot.toObjects(OrderItem::class.java)
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
        orderItem: OrderItem,
    ): ValueOrException<Boolean> {
        return try {
            firestore.collection("order_items").add(orderItem).addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                firestore.collection("order_items").document(docId)
                    .update(
                        hashMapOf(
                            "id" to docId,
                            "status_id" to 3
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
        return try {
            firestore.collection("order_items").document(orderItemId).delete()
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

    override suspend fun updateOrderItem(
        orderItem: OrderItem,
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
            firestore.collection("order_items").document(orderItem.id.toString()).update(orderItemToUpdate)
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }
}