package com.example.onlab.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.DataOrException
import com.example.onlab.model.MProduct
import com.example.onlab.repository.FireRepository
import com.example.onlab.repository.ProductFireRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MProductViewModel @Inject constructor(private val repository: ProductFireRepository, private val fireRepository: FireRepository): ViewModel(){

    val searchText = mutableStateOf("")

    val data: MutableState<DataOrException<List<MProduct>, Boolean, Exception>> = mutableStateOf(
        DataOrException(listOf(), true, Exception(""))
    )

    init {
        getAllProductsFromDB()
    }

    fun getAllProductsFromDB() {
        viewModelScope.launch {
            data.value.loading = true
            val productsResult = repository.getAllProductsFromDatabase()
            if (productsResult.data?.isNotEmpty() == true) {
                val products = productsResult.data
                val filteredProdcuts = if (searchText.value.isBlank()) {
                    products
                } else {
                    products?.filter { it.doesMatchSearchQuery(searchText.value) }
                }
                data.value = DataOrException(filteredProdcuts, false, null)
            } else {
                data.value = DataOrException(emptyList(), false, productsResult.e)
            }
            data.value.loading = false
        }
    }

    fun saveProductToFirebase(product: MProduct, onSuccess: () -> Unit, onFailure: () -> Unit = {}){
        val db = FirebaseFirestore.getInstance()
        val dbCollection = db.collection("products")
        Log.d("product_image", "saveProductToFirebase: ${product.image}")

        if(product.toString().isNotEmpty()){
            dbCollection.add(product)
                .addOnSuccessListener{ documentRef->
                    val docId = documentRef.id
                    viewModelScope.launch {
                        dbCollection.document(docId).update(
                            if (product.image == ""){
                                hashMapOf(
                                    "product_image" to "https://firebasestorage.googleapis.com/v0/b/orderapp-7d65f.appspot.com/o/images%2F1684741663752_image_08c2f5eb-e131-424d-9d52-5490dff6d3de.jpg?alt=media&token=63251bd3-1549-4534-ad1e-30239d40cc0d"
                                ) as Map<String, Any>
                            } else {
                                hashMapOf(
                                    "product_image" to fireRepository.addImageToFirebaseStorage(product.image.toUri())
                                ) as Map<String, Any>
                            }
                        )
                    }
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
        getAllProductsFromDB()
    }

    fun updateProduct(productToUpdate: Map<String, String?>, productID: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        FirebaseFirestore.getInstance().collection("products").document(productID).update(productToUpdate).addOnCompleteListener{ task->
            if (task.isSuccessful){
                onSuccess()
            }
        }.addOnFailureListener{
            onFailure()
        }
        if(!productToUpdate["product_image"]!!.startsWith("https")){
            viewModelScope.launch {
                FirebaseFirestore.getInstance().collection("customers").document(productID).update(
                    hashMapOf(
                        "product_image" to fireRepository.addImageToFirebaseStorage(productToUpdate["product_image"]!!.toUri())
                    ) as Map<String, Any>
                )
            }
        }
        getAllProductsFromDB()
    }

    fun deleteProduct(productID: String, onSuccess: () -> Unit) {
        FirebaseFirestore.getInstance().collection("products").document(productID).delete().addOnCompleteListener {
            if(it.isSuccessful){
                getAllProductsFromDB()
                onSuccess()
            }

        }.addOnFailureListener {
            Log.d("fail", "deleteCustomer: nem lett torolve")
        }
    }

    fun onSearchTextChanged(newText: String) {
        searchText.value = newText
        getAllProductsFromDB()
    }

    fun getProductsByCategory(category: String): List<MProduct>? {
        return data.value.data!!.filter {
            it.category == category
        }
    }

    fun getProductById(productId: String): MProduct? {
        return data.value.data!!.find {
            it.id.toString() == productId
        }
    }

    fun getAllProductsFromDatabase(): List<MProduct>? {
        return data.value.data
    }

}