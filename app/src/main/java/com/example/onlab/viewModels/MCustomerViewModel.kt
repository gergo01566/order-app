package com.example.onlab.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.onlab.data.DataOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.repository.FireRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MCustomerViewModel @Inject constructor( private val repository: FireRepository) : ViewModel() {

    val searchText = mutableStateOf("")

    val data: MutableState<DataOrException<List<MCustomer>, Boolean, Exception>> = mutableStateOf(
        DataOrException(listOf(), true, Exception("")))

    init {
        getAllCustomersFromDatabase()
    }

    fun getAllCustomersFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            val customersResult = repository.getAllCustomersFromDatabase()
            if (customersResult.data?.isNotEmpty() == true) {
                val customers = customersResult.data
                val filteredCustomers = if (searchText.value.isBlank()) {
                    customers
                } else {
                    customers?.filter { it.doesMatchSearchQuery(searchText.value) }
                }
                data.value = DataOrException(filteredCustomers, false, null)
            } else {
                    data.value = DataOrException(emptyList(), false, customersResult.e)
            }
            data.value.loading = false
        }
    }

    fun updateCustomer(customerToUpdate: Map<String, String?>, customerID: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        FirebaseFirestore.getInstance().collection("customers").document(customerID.toString()).update(customerToUpdate).addOnCompleteListener{ task->
            if (task.isSuccessful){
                onSuccess()
            }
        }.addOnFailureListener{
            onFailure()
        }
        getAllCustomersFromDatabase()
    }

    fun deleteCustomer(customerID: String?, onSuccess: () -> Unit) {
        FirebaseFirestore.getInstance().collection("customers").document(customerID.toString()).delete().addOnCompleteListener {
            if(it.isSuccessful){
                getAllCustomersFromDatabase()
                onSuccess()
            }
        }
    }

    fun saveCustomerToFirebase(customer: MCustomer, onSuccess: () -> Unit, onFailure: () -> Unit = {}){
        val db = FirebaseFirestore.getInstance()
        val dbCollection = db.collection("customers")
        var url = ""
        viewModelScope.launch {
            url = repository.addImageToFirebaseStorage(customer.image.toUri())
        }

        if(customer.toString().isNotEmpty()){
            dbCollection.add(customer)
                .addOnSuccessListener{ documentRef->
                    val docId = documentRef.id

                    dbCollection.document(docId)
                        .update(hashMapOf(
                            "id" to docId,
                            "customer_image" to url
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
        getAllCustomersFromDatabase()
    }

    fun onSearchTextChanged(newText: String) {
        searchText.value = newText
        getAllCustomersFromDatabase()
    }







}