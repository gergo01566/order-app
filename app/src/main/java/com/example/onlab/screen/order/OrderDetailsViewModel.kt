package com.example.onlab.screen.order

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.R
import com.example.onlab.components.SnackbarManager
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Customer
import com.example.onlab.model.Order
import com.example.onlab.model.OrderItem
import com.example.onlab.model.Product
import com.example.onlab.navigation.DestinationOneArg
import com.example.onlab.navigation.DestinationTwoArg
import com.example.onlab.repository.OrderItemsRepository
import com.example.onlab.service.CustomerStorageService
import com.example.onlab.service.OrderItemStorageService
import com.example.onlab.service.OrderStorageService
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import com.example.onlab.R.string as AppText

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderItemStorageService: OrderItemStorageService,
    private val orderStorageService: OrderStorageService,
    private val productStorageService: ProductStorageService,
    private val memoryOrderItemsRepository: OrderItemsRepository,
    private val customerStorageService: CustomerStorageService,
    savedStateHandle: SavedStateHandle
): OrderAppViewModel() {

    var orderItemsResponse by mutableStateOf<ValueOrException<List<OrderItem>>>(ValueOrException.Loading)
        private set

    var saveOrderItemResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var saveOrderToFirebase by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var productsResponse by mutableStateOf<ValueOrException<List<Product>>>(ValueOrException.Loading)
        private set

    var deleteOrderItemResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var customerResponse by mutableStateOf<ValueOrException<Customer>>(ValueOrException.Loading)
        private set

    var orderResponse by mutableStateOf<ValueOrException<Order>>(ValueOrException.Loading)
        private set

    private val _orderItemListstate = MutableStateFlow<ValueOrException<List<OrderItem>>>(ValueOrException.Loading)
    val orderItemListState = _orderItemListstate.asStateFlow()

    val customerId = savedStateHandle.get<String>(DestinationOneArg)
    val orderId = savedStateHandle.get<String>(DestinationTwoArg)

    var changeMade by mutableStateOf(false)
        private set

    init {
        loadOrderItems()
        getProducts()
        getCustomer()
        getOrder()
    }

    fun getOrder(){
        launchCatching {
            orderResponse = ValueOrException.Loading
            if (orderId != null) {
                orderStorageService.getOrder(orderId = orderId)
            }
        }
    }

    fun getCustomer(){
        launchCatching {
            customerResponse = ValueOrException.Loading
            customerResponse = customerStorageService.getCustomer(customerId = customerId.toString())
            when(val response = customerResponse){
                is ValueOrException.Failure -> SnackbarManager.displayMessage(AppText.data_loading_error)
                is ValueOrException.Success -> Unit
                else -> {}
            }
        }
    }

    fun loadOrderItems(){
        launchCatching {
            try {
                _orderItemListstate.value = ValueOrException.Loading
                delay(1000)
                orderItemStorageService.getOrderItemsByOrderId(orderId!!).collect{ response->
                    orderItemsResponse = response
                    when (response) {
                        is ValueOrException.Success<List<OrderItem>> -> {
                            memoryOrderItemsRepository.getOrderItemsFromNetwork(response.data)
                            _orderItemListstate.value = ValueOrException.Success(memoryOrderItemsRepository.getOrderItems())
                        }
                        is ValueOrException.Failure -> {
                            _orderItemListstate.value = ValueOrException.Success(memoryOrderItemsRepository.getOrderItems())
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception){
                _orderItemListstate.value = ValueOrException.Failure(e)
            }
        }
    }

    private fun getProducts() {
        launchCatching {
            productsResponse = ValueOrException.Loading
            productStorageService.getAllProducts().collect { response ->
                productsResponse = response
            }
            when(productsResponse){
                is ValueOrException.Failure -> SnackbarManager.displayMessage(AppText.data_loading_error)
                else -> Unit
            }
        }
    }

    fun updateOrderItemLocally(orderItem: OrderItem){
        launchCatching {
            _orderItemListstate.value = ValueOrException.Loading
            delay(500)
            memoryOrderItemsRepository.updateOrderItem(orderItem)
            _orderItemListstate.value = ValueOrException.Success(memoryOrderItemsRepository.getOrderItems())
            SnackbarManager.displayMessage(R.string.save_success)
        }
        changeMade = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewOrder(){
        saveOrderItemResponse = ValueOrException.Loading
        launchCatching {
            val newOrderId = UUID.randomUUID().toString()
            val newOrder = Order(
                orderId = newOrderId,
                date = LocalDate.now().toString(),
                customerID = customerId.toString(),
                status = 0,
                madeby = FirebaseAuth.getInstance().currentUser!!.email!!
            )
            saveOrderItemResponse = orderStorageService.addOrder(newOrder)
            memoryOrderItemsRepository.getOrderItems().forEach { orderItem ->
                orderItem.orderID = newOrderId
                orderItem.statusID = 3
                saveOrderItemResponse = orderItemStorageService.addOrderItem(orderItem)
            }
        }
    }

    fun updateExistingOrder() {
        saveOrderItemResponse = ValueOrException.Loading
        launchCatching {
            memoryOrderItemsRepository.getOrderItems().forEach {
                if (it.statusID == 0) {
                    try {
                        saveOrderItemResponse = orderItemStorageService.updateOrderItem(it)
                    } catch (e: Exception) {
                    }
                }
                if (it.statusID == -1){
                    it.statusID = 3
                    saveOrderItemResponse = orderItemStorageService.addOrderItem(it)
                }
                if (it.amount == 0){
                    saveOrderItemResponse = orderItemStorageService.deleteOrderItem(it.id.toString())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSaveClick() {
        launchCatching {
            when(orderStorageService.getOrder(orderId.toString())){
                is ValueOrException.Failure -> addNewOrder()
                else -> updateExistingOrder()
            }
        }
    }

    fun onNavigateBack(onNoChange:()->Unit, onChange:()->Unit){
        if(changeMade){
            onChange()
        }
        else onNoChange()
    }

    fun generatePDF(
        context: Context,
    ) {
        var orderItems = emptyList<OrderItem>()
        when(val orderItemsResponse = orderItemsResponse){
            is ValueOrException.Success -> {
                orderItems = orderItemsResponse.data
            }
            else -> Unit
        }
        var productsList = emptyList<Product>()
        when(val productsReponse = productsResponse){
            is ValueOrException.Success -> {
                productsList = productsReponse.data
            }
            else -> Unit
        }

        var order = Order()
        when(val orderResponse = orderResponse){
            is ValueOrException.Success -> {
                order = orderResponse.data
            }
            else -> {}
        }

        when(val customerApiResponse = customerResponse){
            is ValueOrException.Success -> {
                val pageHeight = 1120
                val pageWidth = 792
                lateinit var scaledbmp: Bitmap
                val pdfDocument = PdfDocument()
                val paint = Paint()
                val title = Paint()

                val directoryPath = "/storage/emulated/0/Documents"
                val directory = File(directoryPath)
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val fileName = "order_${orderItems[0].orderID}.pdf"
                val file = File(directory, fileName)

                val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.picture_placeholder)
                scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)
                val myPageInfo: PdfDocument.PageInfo? =
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
                val myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)
                val canvas: Canvas = myPage.canvas
                canvas.drawBitmap(scaledbmp, 56F, 40F, paint)
                title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                title.textSize = 20F
                title.color = ContextCompat.getColor(context, R.color.black)
                canvas.drawText("Ügyfél neve: ${customerApiResponse.data.firstName} ${customerApiResponse.data.lastName}", 209F, 40F, title)
                canvas.drawText("Ügyfél címe: ${customerApiResponse.data.address} ", 209F, 75F, title)
                canvas.drawText("Ügyfél telefonszáma: ${customerApiResponse.data.phoneNumber} ", 209F, 110F, title)
                title.textSize = 15F
                canvas.drawText("Rendelési azonosító: ${orderItems[0].orderID}", 209F, 145F, title)
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
                    val product = productsList.find { it.id.toString() == orderItem.productID }
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
                }
            }
            else -> {}
        }

    }
}