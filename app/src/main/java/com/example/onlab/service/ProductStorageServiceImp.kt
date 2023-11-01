package com.example.onlab.service

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.example.onlab.data.DataOrException
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Category
import com.example.onlab.model.MOrder
import com.example.onlab.model.MProduct
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.dataObjects
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
                        searchQuery.isBlank() || product.title?.startsWith(searchQuery, ignoreCase = true) == true
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





    //override val data = MutableStateFlow(DataOrException<List<MProduct>, Boolean, Exception>())

    override val data = MutableStateFlow(DataOrException<List<MProduct>, Boolean, Exception>())

//    override suspend fun getAllProducts() {
//
//        try {
//            data.value.loading = true
//            data.value.data = firestore.collection("products").get().await().documents.map { documentSnapshot ->
//                documentSnapshot.toObject(MProduct::class.java)!!
//            }
//            if(!data.value.data.isNullOrEmpty()) {
//                data.value.loading = false
//            }
//
//        } catch (exception: FirebaseFirestoreException){
//            data.value.e = exception
//        }
//    }

//    override suspend fun getAllProducts(): DataOrException<List<MProduct>,Boolean ,Exception> {
//        // Perform the data retrieval logic here
//        val dataOrException = DataOrException<List<MProduct>, Boolean, Exception>()
//        val result = // Your data retrieval logic
//            if (result.data?.isNotEmpty() == true) {
//                data.value = DataOrException(result.data, false, null)
//            } else {
//                data.value = DataOrException(emptyList(), false, result.e)
//            }
//        return data.value
//    }

//    override suspend fun getAllProducts(): DataOrException<List<MProduct>, Boolean, java.lang.Exception> {
//        val dataOrException = DataOrException<List<MProduct>, Boolean, Exception>()
//
//        try {
//            dataOrException.loading = true
//            dataOrException.data = firestore.collection("products").get().await().documents.map { documentSnapshot ->
//                documentSnapshot.toObject(MProduct::class.java)!!
//            }
//            if(!dataOrException.data.isNullOrEmpty()) {
//                dataOrException.loading = false
//            }
//
//        } catch (exception: FirebaseFirestoreException){
//            dataOrException.e = exception
//        }
//        return dataOrException
//    }

//    override fun getAllProducts() = callbackFlow {
//        val snapshotListener = firestore.collection("products").addSnapshotListener { snapshot, e ->
//            val response = if (snapshot != null) {
//                val products = snapshot.toObjects(MProduct::class.java)
//                ValueOrException.Success(products)
//            } else {
//                ValueOrException.Failure(e)
//            }
//            trySend(response)
//        }
//        awaitClose {
//            snapshotListener.remove()
//        }
//    }

//    override fun getAllProducts() = callbackFlow {
//        var query = firestore.collection("products")
//
//        // Add a filter for the selected category if it's not null
//        if (category != null) {
//            query =
//                firestore.collection("products").whereEqualTo("category", category) as CollectionReference // Assuming "category" is the field name for category
//        }
//
//        val snapshotListener = query.addSnapshotListener { snapshot, e ->
//            val response = if (snapshot != null) {
//                val products = snapshot.documents
//                    .filter { document ->
//                        val title = document.getString("title")
//                        if (title != null) {
//                            title.startsWith(searchQuery ?: "")
//                        } else {
//                            false
//                        }
//                    }
//                    .mapNotNull { it.toObject(MProduct::class.java) }
//                ValueOrException.Success(products)
//            } else {
//                ValueOrException.Failure(e)
//            }
//            trySend(response)
//        }
//
//        awaitClose {
//            snapshotListener.remove()
//        }
//    }




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
        onComplete: () -> Unit
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
                    ).addOnCompleteListener { onComplete() }
                }
            }
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e = e)
        }
    }

    override suspend fun deleteProduct(
        productId: String,
        onComplete: () -> Unit
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
        onComplete: () -> Unit
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




//    override suspend fun getAllProducts(): Flow<List<MProduct>> {
//        val dataOrException = DataOrException<List<MProduct>, Boolean, Exception>()
//
//        try {
//            dataOrException.loading = true
//            dataOrException.data =  firestore.collection("products").get().await().documents.map { documentSnapshot ->
//                documentSnapshot.toObject(MProduct::class.java)!!
//            }
//            if(!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false
//
//        } catch (exception: FirebaseFirestoreException){
//            dataOrException.e = exception
//        }
//        return firestore.collection("products").get().await().documents.map { documentSnapshot ->
//                documentSnapshot.toObject(MProduct::class.java)!!
//        }
        //return firestore.collection("products").dataObjects<MProduct>()


//    override val products: Flow<List<MProduct>> = if (searchQuery.isEmpty()) {
//        if (category == null) {
//            firestore.collection("products").dataObjects<MProduct>().onCompletion {
//
//            }
//        } else {
//            firestore.collection("products").whereEqualTo("category", category.toString()).dataObjects<MProduct>().onCompletion {
//            }
//        }
//    } else {
//        val query = firestore.collection("products")
//        if (category != null) {
//            query.whereEqualTo("category", category.toString())
//        }
//        firestore.collection("products").dataObjects<MProduct>().filter { product -> product.any { it.doesMatchSearchQuery(searchQuery) } }.onCompletion {
//        }
//    }

