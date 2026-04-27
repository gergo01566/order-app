package com.example.orderapp.model.service.imp

import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.Category
import com.example.orderapp.model.Product
import com.example.orderapp.model.service.ProductStorageService
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
        val baseQuery = firestore.collection("products")
        val query = if (category != null) {
            baseQuery.whereEqualTo("category", category)
        } else {
            baseQuery
        }

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(ValueOrException.Failure(e))
                return@addSnapshotListener
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
        val baseQuery = firestore.collection("products")
        val query = if (category != null && category.name != "Összes") {
            baseQuery.whereEqualTo("product_category", category.name)
        } else {
            baseQuery
        }

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                val products = snapshot.toObjects(Product::class.java)
                    .filter { product ->
                        val matchesSearch = text.isNullOrEmpty() || product.doesMatchSearchQuery(text)
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
            val documentRef = firestore.collection("products").add(product).await()
            val docId = documentRef.id
            firestore.collection("products").document(docId).update(
                mapOf(
                    "id" to docId,
                    "product_image" to "https://firebasestorage.googleapis.com/v0/b/orderapp-7d65f.appspot.com/o/images%2F1684741663752_image_08c2f5eb-e131-424d-9d52-5490dff6d3de.jpg?alt=media&token=63251bd3-1549-4534-ad1e-30239d40cc0d"
                )
            ).await()
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