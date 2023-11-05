package com.example.onlab.service.imp

import androidx.core.net.toUri
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.MProduct
import com.example.onlab.service.ProductStorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductStorageServiceImp
@Inject
constructor(private val firestore: FirebaseFirestore, private val storage: FirebaseStorage) :
    ProductStorageService {

    override var searchQuery: String = ""

    override val category: Category? = null

    override fun getAllProducts() = callbackFlow {
        val query = firestore.collection("products")

        // Add a filter for the selected category if it's not null
        category?.let { selectedCategory ->
            query.whereEqualTo("category", selectedCategory)
        }

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                if (e.equals(FirebaseFirestoreException.Code.UNAVAILABLE)){
                    ValueOrException.Failure(e)
                }
            }
            val response = if (snapshot != null && !snapshot.isEmpty) {
                val products = snapshot.toObjects(MProduct::class.java)
                    .filter { product ->
                        searchQuery.isBlank() || product.title.startsWith(searchQuery, ignoreCase = true)
                    }
                ValueOrException.Success(products)
            } else {
                ValueOrException.Failure(e)
            }
            trySend(response)
        }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getProductsByCategoryAndText(
        text: String?,
        category: Category?
    ) = callbackFlow {
        val query = firestore.collection("products")

        // Add a filter for the selected category if it's not equal to "Osszes"
        if (category?.name != "Összes") {
            query.whereEqualTo("product_category", category?.name)
        }

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                val products = snapshot.toObjects(MProduct::class.java)
                    .filter { product ->
                        val matchesSearch = text!!.isEmpty() || product.doesMatchSearchQuery(text)
                        val matchesCategory = category == null || category == Category.Összes || product.category == category.toString()
                        matchesCategory && matchesSearch
                    }
                ValueOrException.Success(products)
            } else {
                ValueOrException.Failure(e)
            }
            trySend(response)
        }


        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getProduct(productId: String): ValueOrException<MProduct> {
        return try {
            val documentSnapshot =
                firestore.collection("products").document(productId).get().await()
            if (documentSnapshot.exists()) {
                val product = documentSnapshot.toObject<MProduct>()
                ValueOrException.Success(product!!)
            } else {
                ValueOrException.Failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }


    override suspend fun saveProduct(
        product: MProduct,
    ): ValueOrException<Boolean> {
        return try {
            firestore.collection("products").add(product).addOnSuccessListener { documentRef ->
                firestore.collection("products").document(documentRef.id).update(
                    (if (product.image == "") {
                        hashMapOf(
                            "product_image" to "https://firebasestorage.googleapis.com/v0/b/orderapp-7d65f.appspot.com/o/images%2F1684741663752_image_08c2f5eb-e131-424d-9d52-5490dff6d3de.jpg?alt=media&token=63251bd3-1549-4534-ad1e-30239d40cc0d"
                        )
                    } else {
                        val fileName =
                            "${System.currentTimeMillis()}_${product.image.toUri().lastPathSegment}"
                        val imageRef = storage.reference.child("images/${fileName}")

                        val downloadUrl = imageRef.downloadUrl.toString()
                        val imageDoc = hashMapOf("url" to downloadUrl)

                        firestore.collection("images").document(fileName).set(imageDoc)
                        hashMapOf(
                            "product_image" to downloadUrl
                        )
                    }) as Map<String, Any>
                ).addOnSuccessListener {
                    firestore.collection("products").document(documentRef.id).update(
                        hashMapOf(
                            "id" to documentRef.id,
                        ) as Map<String, Any>
                    ).addOnCompleteListener { ValueOrException.Success(true) }
                }
            }
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e = e)
        }
    }

    override suspend fun deleteProduct(
        productId: String,
    ): ValueOrException<Boolean> {
        return try {
            firestore.collection("products").document(productId).delete().await()
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

    override suspend fun updateProduct(
        product: MProduct,
    ): ValueOrException<Boolean> {
        try {
            val productToUpdate = mapOf(
                "product_title" to product.title,
                "product_category" to product.category,
                "price_piece" to product.pricePerPiece,
                "price_carton" to product.pricePerKarton,
                "product_image" to if (product.image.startsWith("https")) {
                    product.image.toUri()
                } else {
                    product.image
                }
            )
            firestore.collection("products").document(product.id!!).update(productToUpdate).await()
            return ValueOrException.Success(true)
        } catch (e: Exception) {
            return ValueOrException.Failure(e)
        }
    }

}