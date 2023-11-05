package com.example.onlab.service.imp

import androidx.core.net.toUri
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.service.CustomerStorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CustomerStorageServiceImp
@Inject
constructor(private val firestore: FirebaseFirestore, private val storage: FirebaseStorage) :
    CustomerStorageService {

    override var searchQuery: String = ""

    override fun getAllCustomers() = callbackFlow {
        val query = firestore.collection("customers")

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                if (e.equals(FirebaseFirestoreException.Code.UNAVAILABLE)){
                    ValueOrException.Failure(e)
                }
            }
            val response = if (snapshot != null && !snapshot.isEmpty) {
                val customers = snapshot.toObjects(MCustomer::class.java)
                    .filter { customer ->
                        searchQuery.isBlank() || customer.firstName.startsWith(searchQuery, ignoreCase = true)
                    }
                ValueOrException.Success(customers)
            } else {
                ValueOrException.Failure(e)
            }
            trySend(response)
        }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getCustomersByText(
        text: String?,
    ) = callbackFlow {
        val query = firestore.collection("customers")

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                val customers = snapshot.toObjects(MCustomer::class.java)
                    .filter { customer ->
                        text!!.isEmpty() || customer.doesMatchSearchQuery(text)
                    }
                ValueOrException.Success(customers)
            } else {
                ValueOrException.Failure(e)
            }
            trySend(response)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getCustomer(customerId: String): ValueOrException<MCustomer> {
        return try {
            val documentSnapshot =
                firestore.collection("customers").document(customerId).get().await()
            if (documentSnapshot.exists()) {
                val customer = documentSnapshot.toObject<MCustomer>()
                ValueOrException.Success(customer!!)
            } else {
                ValueOrException.Failure(Exception("Customer not found"))
            }
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }


    override suspend fun addCustomer(
        customer: MCustomer,
    ): ValueOrException<Boolean> {
        return try {
            firestore.collection("customers").add(customer).addOnSuccessListener { documentRef ->
                firestore.collection("customers").document(documentRef.id).update(
                    (if (customer.image == "") {
                        hashMapOf(
                            "customer_image" to "https://firebasestorage.googleapis.com/v0/b/orderapp-7d65f.appspot.com/o/images%2F1684741663752_image_08c2f5eb-e131-424d-9d52-5490dff6d3de.jpg?alt=media&token=63251bd3-1549-4534-ad1e-30239d40cc0d"
                        )
                    } else {
                        val fileName =
                            "${System.currentTimeMillis()}_${customer.image.toUri().lastPathSegment}"
                        val imageRef = storage.reference.child("images/${fileName}")

                        val downloadUrl = imageRef.downloadUrl.toString()
                        val imageDoc = hashMapOf("url" to downloadUrl)

                        firestore.collection("images").document(fileName).set(imageDoc)
                        hashMapOf(
                            "customer_image" to downloadUrl
                        )
                    }) as Map<String, Any>
                ).addOnSuccessListener {
                    firestore.collection("customers").document(documentRef.id).update(
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

    override suspend fun deleteCustomer(
        customerId: String,
    ): ValueOrException<Boolean> {
        return try {
            firestore.collection("customers").document(customerId).delete().await()
            ValueOrException.Success(true)
        } catch (e: Exception) {
            ValueOrException.Failure(e)
        }
    }

    override suspend fun updateCustomer(
        customer: MCustomer,
    ): ValueOrException<Boolean> {
        try {
            val customerToUpdate = mapOf(
                "first_name" to customer.firstName,
                "last_name" to customer.lastName,
                "customer_address" to customer.address,
                "phone_number" to customer.phoneNumber,
                "customer_image" to if (customer.image.startsWith("https")) {
                    customer.image.toUri()
                } else {
                    customer.image
                }
            )
            firestore.collection("customers").document(customer.id!!).update(customerToUpdate).await()
            return ValueOrException.Success(true)
        } catch (e: Exception) {
            return ValueOrException.Failure(e)
        }
    }

}