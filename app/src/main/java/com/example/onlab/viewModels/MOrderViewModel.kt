package com.example.onlab.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.DataOrException
import com.example.onlab.model.MOrder
import com.example.onlab.model.MProduct
import com.example.onlab.model.Order
import com.example.onlab.repository.FireRepository
import com.example.onlab.repository.OrderFireRepository
import com.example.onlab.repository.ProductFireRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MOrderViewModel @Inject constructor(private val repository: OrderFireRepository, private val fireRepository: FireRepository): ViewModel(){
    val data: MutableState<DataOrException<List<MOrder>, Boolean, Exception>> = mutableStateOf(
        DataOrException(listOf(), true, Exception(""))
    )

    init {
        getAllOrdersFromDatabase()
    }

    private fun getAllOrdersFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            val ordersResult = repository.getAllOrders()
            if (ordersResult.data?.isNotEmpty() == true) {
                val orders = ordersResult.data
                data.value = DataOrException(orders, false, null)
            } else {
                data.value = DataOrException(emptyList(), false, ordersResult.e)
            }
            data.value.loading = false
        }
    }

    fun saveOrderToFirebase(order: MOrder, onSuccess: () -> Unit, onFailure: () -> Unit = {}){
        val db = FirebaseFirestore.getInstance()
        val dbCollection = db.collection("orders")

        if(order.toString().isNotEmpty()){
            dbCollection.add(order)
                .addOnSuccessListener{ documentRef->
                    val docId = documentRef.id
                    dbCollection.document(docId)
                        .update(hashMapOf(
                            "id" to docId,
                        ) as Map<String, Any>)
                        .addOnCompleteListener{task->
                            if(task.isSuccessful){
                                onSuccess()
                            }
                        }
                        .addOnFailureListener{
                            Log.d("FB", "saveToFirebase: Error: $docId")
                        }
                }
        }
        getAllOrdersFromDatabase()
    }

    fun updateorder(orderToUpdate: Map<String, Any?>, orderId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        FirebaseFirestore.getInstance().collection("orders").document(orderId).update(orderToUpdate).addOnCompleteListener{ task->
            if (task.isSuccessful){
                onSuccess()
            }
        }.addOnFailureListener{
            onFailure()
        }
        getAllOrdersFromDatabase()
    }

    fun deleteOrder(orderId: String, onSuccess: () -> Unit) {
        FirebaseFirestore.getInstance().collection("orders").document(orderId).delete().addOnCompleteListener {
            if(it.isSuccessful){
                getAllOrdersFromDatabase()
                onSuccess()
            }
        }.addOnFailureListener {
            Log.d("fail", "deleteCustomer: nem lett torolve")
        }
    }

    fun getOrdersByStatus(status: Int): List<MOrder> {
        return data.value.data!!.filter {
            it.status == status
        }
    }
}