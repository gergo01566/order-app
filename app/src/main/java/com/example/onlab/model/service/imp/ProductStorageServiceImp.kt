package com.example.onlab.model.service.imp

import com.example.onlab.model.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.Product
import com.example.onlab.model.service.ProductStorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
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
                val products = snapshot.toObjects(Product::class.java)
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
                val products = snapshot.toObjects(Product::class.java)
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

    override suspend fun getProduct(productId: String): ValueOrException<Product> {
        return try {
            val documentSnapshot =
                firestore.collection("products").document(productId).get().await()
            if (documentSnapshot.exists()) {
                val product = documentSnapshot.toObject<Product>()
                ValueOrException.Success(product!!)
            } else {
                ValueOrException.Failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }


    override suspend fun saveProduct(
        product: Product,
    ): ValueOrException<Boolean> {
        return try {
            var docId = ""
            firestore.collection("products").add(product).addOnSuccessListener { documentRef ->
                docId = documentRef.id
                firestore.collection("products").document(documentRef.id).update(
                    hashMapOf(
                        "id" to documentRef.id,
                        "product_image" to "https://firebasestorage.googleapis.com/v0/b/orderapp-7d65f.appspot.com/o/images%2F1684741663752_image_08c2f5eb-e131-424d-9d52-5490dff6d3de.jpg?alt=media&token=63251bd3-1549-4534-ad1e-30239d40cc0d"
                    ) as Map<String, Any>
                ).addOnCompleteListener { ValueOrException.Success(true) }
            }.await()
            if (!product.image.startsWith("https")) {
                storage.uploadAndGetDownloadUrl("products", docId, "product_image", product.image)
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
        product: Product,
    ): ValueOrException<Boolean> {
        return try {
            val productToUpdate = mapOf(
                "product_title" to product.title,
                "product_category" to product.category,
                "price_piece" to product.pricePerPiece,
                "price_carton" to product.pricePerKarton,
            )
            firestore.collection("products").document(product.id!!).update(productToUpdate).await()
            if (!product.image.startsWith("https")) {
                storage.uploadAndGetDownloadUrl("products", product.id, "product_image", product.image)
            }
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

}