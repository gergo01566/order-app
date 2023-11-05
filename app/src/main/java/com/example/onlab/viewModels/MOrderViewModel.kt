package com.example.onlab.viewModels

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.R
import com.example.onlab.data.DataOrException
import com.example.onlab.model.*
import com.example.onlab.repository.FireRepository
import com.example.onlab.repository.OrderFireRepository
import com.example.onlab.repository.ProductFireRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import android.Manifest
import android.app.Activity
import android.content.Intent

import android.content.pm.PackageManager
import android.graphics.*

import androidx.activity.ComponentActivity

import androidx.compose.foundation.layout.*
import androidx.compose.material.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider


@HiltViewModel
class MOrderViewModel @Inject constructor(private val repository: OrderFireRepository, private val fireRepository: FireRepository): ViewModel(){

    val data: MutableState<DataOrException<List<MOrder>, Boolean, Exception>> = mutableStateOf(
        DataOrException(listOf(), true, Exception(""))
    )

    init {
        getAllOrdersFromDatabase()
    }

    fun getAllOrdersFromDatabase() {
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

    fun deleteUnrelevantOrders(){
        viewModelScope.launch {
            val customerIds = fireRepository.getAllCustomersFromDatabase().data!!.map { it.id }


            data.value.data?.filter { it.orderId != null }
                ?.filter { it.orderId != null && it.customerID !in customerIds!! }
                ?.forEach { order ->
                    order.orderId?.let { orderId ->
                        deleteOrder(orderId){}
                    }
                }
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

    fun isOrderIncluded(id: String): Boolean {
        return data.value.data!!.any {
            (it.orderId == id)
        }!!
    }

    fun getOrdersByStatus(status: Int): List<MOrder> {
        return data.value.data!!.filter {
            it.status == status
        }
    }

    fun getOrdersByCustomer(customerId: String): List<MOrder> {
        return data.value.data!!.filter {
            it.customerID == customerId
        }
    }fun generatePDF(
        context: Context,
        orderId: String,
        customerViewModel: MCustomerViewModel,
        mOrderItemViewModel: MOrderItemViewModel,
        mProductViewModel: MProductViewModel,
    ) {
        val order = data.value.data!!.first { it.orderId == orderId }
        val customer = customerViewModel.getCustomerById(order.customerID)
        val orderItems = mOrderItemViewModel.getOrderItemsByOrder(orderId)
        val products = orderItems.map { mProductViewModel.getProductById(it.productID) }

        val pageHeight = 1120
        val pageWidth = 792
        lateinit var scaledbmp: Bitmap
        val pdfDocument: PdfDocument = PdfDocument()
        val paint: Paint = Paint()
        val title: Paint = Paint()

        val directoryPath = "/storage/emulated/0/Documents"
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val fileName = "order_$orderId.pdf"
        val file = File(directory, fileName)

        var bmp: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.picture_placeholder)
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)
        val myPageInfo: PdfDocument.PageInfo? =
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)
        val canvas: Canvas = myPage.canvas
        canvas.drawBitmap(scaledbmp, 56F, 40F, paint)
        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        title.textSize = 20F
        title.color = ContextCompat.getColor(context, R.color.black)
        canvas.drawText("Ügyfél neve: ${customer?.lastName} ${customer?.firstName}", 209F, 40F, title)
        canvas.drawText("Ügyfél címe: ${customer?.address} ", 209F, 75F, title)
        canvas.drawText("Ügyfél telefonszáma: ${customer?.phoneNumber} ", 209F, 110F, title)
        title.textSize = 15F
        canvas.drawText("Rendelési azonosító: ${order.orderId}", 209F, 145F, title)
        canvas.drawText("Rendelés kelte: ${order.date}", 209F, 180F, title)
        title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        title.color = ContextCompat.getColor(context, R.color.black)
        title.textSize = 15F
        title.textAlign = Paint.Align.CENTER

        canvas.drawText("Termék neve ", 170F, 270F, title)
        canvas.drawText("Mennyiség ", 340F, 270F, title)
        canvas.drawText("Darab", 510F, 270F, title)
        canvas.drawText("Karton", 680F, 270f, title)
        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        var yPosition = 300f
        for (orderItem in orderItems) {
            val product = products.find { it!!.id.toString() == orderItem.productID }
            canvas.drawText(product!!.title, 170F, yPosition, title)
            canvas.drawText("${orderItem.amount}", 340F, yPosition , title)
            canvas.drawText((if (orderItem.piece){"igen"} else {
                "nem"
            }).toString(), 510F, yPosition , title)
            canvas.drawText((if (orderItem.carton){"igen"} else {
                "nem"
            }).toString(), 680F, yPosition , title)
            canvas.drawLine(70F, yPosition + 30, 722F, yPosition + 30, paint)
            yPosition += 60F
        }
        var sikerult = false

        pdfDocument.finishPage(myPage)

        // Save the PDF file
        try {
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            sikerult = true
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show()
        }

// Close the PDF document
        pdfDocument.close()
        if(sikerult){
            Toast.makeText(context, "PDF elmentve", Toast.LENGTH_SHORT).show()
            sikerult = false
        }
    }

}