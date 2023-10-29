package com.example.onlab.service

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.example.onlab.data.DataOrException
import com.example.onlab.model.Category
import com.example.onlab.model.MOrder
import com.example.onlab.model.MProduct
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.dataObjects
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductStorageServiceImp
@Inject
constructor(private val firestore: FirebaseFirestore, private val storage: FirebaseStorage) :
ProductStorageService  {

    override var searchQuery: String = ""

    override val category: Category? = null

    override var loading: MutableStateFlow<Boolean> = true




    override val products: Flow<List<MProduct>> = if (searchQuery.isEmpty()) {
        loading = true
        if (category == null) {
            firestore.collection("products").dataObjects<MProduct>().onCompletion {
                loading = false
            }
        } else {
            firestore.collection("products").whereEqualTo("category", category.toString()).dataObjects<MProduct>().onCompletion {
                loading.value = false
            }
        }
    } else {
        val query = firestore.collection("products")
        if (category != null) {
            query.whereEqualTo("category", category.toString())
        }
        firestore.collection("products").dataObjects<MProduct>().filter { product -> product.any { it.doesMatchSearchQuery(searchQuery) } }.onCompletion {
            loading.value = false
        }
    }.onCompletion { loading.value = false }


    override suspend fun getProduct(productId: String): MProduct? {
        return firestore.collection("products").document(productId).get().await().toObject()
    }

    override suspend fun updateProduct(product: MProduct, onComplete: () -> Unit) {
        val productToUpdate = hashMapOf(
            "product_title" to product.title,
            "product_category" to product.category,
            "price_piece" to product.pricePerPiece,
            "price_carton" to product.pricePerKarton,
            "product_image" to if (product.image.startsWith("https")) {
                product.image.toUri()
            } else {
                product.image
            }
        ).toMap()
        firestore.collection("products").document(product.id!!).update(productToUpdate).addOnCompleteListener { onComplete() }
    }

    override suspend fun saveProduct(product: MProduct, onComplete: () -> Unit) {
        firestore.collection("products").add(product).addOnSuccessListener { documentRef->
            firestore.collection("products").document(documentRef.id).update(
                (if (product.image == ""){
                    hashMapOf(
                        "product_image" to "https://firebasestorage.googleapis.com/v0/b/orderapp-7d65f.appspot.com/o/images%2F1684741663752_image_08c2f5eb-e131-424d-9d52-5490dff6d3de.jpg?alt=media&token=63251bd3-1549-4534-ad1e-30239d40cc0d"
                    )
                } else {
                    val fileName = "${System.currentTimeMillis()}_${product.image.toUri().lastPathSegment}"
                    val imageRef = storage.reference.child("images/${fileName}")

                    val downloadUrl = imageRef.downloadUrl.toString()
                    val imageDoc = hashMapOf("url" to downloadUrl)

                    firestore.collection("images").document(fileName).set(imageDoc)
                    hashMapOf(
                        "product_image" to downloadUrl
                    )
                }) as Map<String, Any>
            )
            firestore.collection("products").document(documentRef.id).update(
                hashMapOf(
                    "id" to documentRef.id,
                ) as Map<String, Any>
            ).addOnCompleteListener { onComplete() }
        }
    }

    override suspend fun deleteProduct(productId: String, onComplete: () -> Unit){
        firestore.collection("products").document(productId).delete().addOnCompleteListener { onComplete() }
    }
}