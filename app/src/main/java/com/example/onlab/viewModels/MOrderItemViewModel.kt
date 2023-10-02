package com.example.onlab.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.DataOrException
import com.example.onlab.model.MOrderItem
import com.example.onlab.model.MProduct
import com.example.onlab.repository.FireRepository
import com.example.onlab.repository.OrderItemFireRepository
import com.example.onlab.repository.ProductFireRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MOrderItemViewModel @Inject constructor(private val repository: OrderItemFireRepository, private val fireRepository: FireRepository): ViewModel() {

    val data: MutableState<DataOrException<List<MOrderItem>, Boolean, Exception>> = mutableStateOf(
        DataOrException(listOf(), true, Exception(""))
    )

    init {
        getOrderItemsFromDatabase()
    }

    fun getOrderItemsFromDatabase(orderId: String? = null) {
        viewModelScope.launch {
            data.value.loading = true
            val orderItemsResult = repository.getAllOrderItemsFromDatabase()
            if (orderItemsResult.data?.isNotEmpty() == true) {
                val orderItemsByOrderId = orderItemsResult.data
                val filteredOrderItems = if (orderId.isNullOrEmpty()) {
                    orderItemsByOrderId
                } else {
                    orderItemsByOrderId?.filter {
                        it.orderID == orderId
                    }
                }
                data.value = DataOrException(filteredOrderItems, false, null)
            } else {
                data.value = DataOrException(emptyList(), false, orderItemsResult.e)
            }
            data.value.loading = false
        }
    }

    fun saveOrderItemToFirebase(
        orderItem: MOrderItem,
        onSuccess: () -> Unit,
        onFailure: () -> Unit = {}
    ) {
        val db = FirebaseFirestore.getInstance()
        val dbCollection = db.collection("order_items")

        if (orderItem.toString().isNotEmpty()) {
            dbCollection.add(orderItem)
                .addOnSuccessListener { documentRef ->
                    val docId = documentRef.id
                    dbCollection.document(docId)
                        .update(
                            hashMapOf(
                                "id" to docId,
                            ) as Map<String, Any>
                        )
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onSuccess()
                            }
                        }
                        .addOnFailureListener {
                            Log.d("FB", "saveToFirebase: Error: $docId")
                        }
                }
        }
        getOrderItemsFromDatabase()
    }

    fun updateOrderItem(orderItemToUpdate: Map<String, Any?>, orderItemId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        FirebaseFirestore.getInstance().collection("order_items").document(orderItemId).update(orderItemToUpdate).addOnCompleteListener{ task->
            if (task.isSuccessful){
                getOrderItemsFromDatabase()
                onSuccess()
            }
        }.addOnFailureListener{
            onFailure()
        }
    }

    fun deleteOrderItem(orderItemId: String, onSuccess: () -> Unit) {
        FirebaseFirestore.getInstance().collection("order_items").document(orderItemId).delete().addOnCompleteListener {
            if(it.isSuccessful){
                getOrderItemsFromDatabase()
                onSuccess()
            }
        }.addOnFailureListener {
            Log.d("fail", "deleteCustomer: nem lett torolve")
        }
    }

    fun getOrderItemsByOrder(orderId: String): List<MOrderItem> {
        getOrderItemsFromDatabase()
        return data.value.data!!.filter {
            it.orderID == orderId
        }
    }

}