package com.example.onlab.screen.product

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.model.*
import java.util.*
import com.example.onlab.model.Category as Categ
import com.example.onlab.data.ValueOrException
import com.example.onlab.navigation.DestinationProductDetails
import com.example.onlab.navigation.DestinationProductList
import com.example.onlab.screen.customer.AddButton
import com.example.onlab.screen.customer.LoadingScreen
import com.example.onlab.screen.customer.SearchBar
import com.example.onlab.screen.order.OrderDetailsViewModel


@Composable
fun ProductListScreen(
    onNavigate: (String) -> Unit,
    navigateFromTo: (String, String) -> Unit,
    navigateBack: () -> Unit,
    onAddOrderItem: (MOrderItem) -> Unit,
    navigateBackToOrder: (String, String) ->Unit,
    orderDetailsViewModel: OrderDetailsViewModel = hiltViewModel(),
    productListViewModel: ProductListViewModel = hiltViewModel(),
    state: List<MOrderItem>
) {
    Log.d("update", "productlist: $state") // Log the value

    val showDialog = remember { mutableStateOf(false) }
    val showFullScreenDialog = remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<MProduct?>(null) }
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<Categ?>(com.example.onlab.model.Category.Összes) }

    Scaffold(
        topBar = {
            if (productListViewModel.isOrdering)
                createTopBar( text = "Új rendelés", withIcon = true, onBack = { navigateBack() })
            else {
                createTopBar(text = "Termékek", withIcon = false){ navigateBack() }
            }
        },
        bottomBar = {
            if (!productListViewModel.isOrdering) BottomNavBar(selectedItem = items[2]){ navigateFromTo(DestinationProductList, it) }
        },
        floatingActionButton = {
            if(!productListViewModel.isOrdering){
                AddButton {
                    navigateFromTo(DestinationProductList, DestinationProductDetails)
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = { it ->
            when(val productsResponse = productListViewModel.productsResponse){
                is ValueOrException.Loading -> LoadingScreen()
                is ValueOrException.Failure -> Snackbar { Text(text = "Nem sikerült törölni a terméket") }
                is ValueOrException.Success -> {
                    it.calculateBottomPadding()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = it.calculateBottomPadding())
                    ) {
                        MenuBar(
                            categories = getCategoryTypes(com.example.onlab.model.Category::class.java),
                            selectedCategory = selectedCategory,
                            onCategorySelected = { category ->
                                productListViewModel.onCategoryChanged(category = category!!)
                                selectedCategory = category
                            }
                        )
                        SearchBar(onSearchTextChanged = { newText ->
                            productListViewModel.onSearchTextChanged(newText)
                        })
                        ProductList(
                            data = productsResponse.data,
                            onDelete = {
                                showDialog.value = true
                                selectedProduct = it
                            },
                            onEdit = {
                                onNavigate(it.id.toString())
                            },
                            onAddToOrder = {
                                if(productListViewModel.isOrdering) {
                                    selectedProduct = it
                                    showFullScreenDialog.value = true
                                }
                                else{
                                    Log.d("TAG", "ProductListScreen: ez nem jott ossze")
                                }
                            },
                            iconClickEnabled = !productListViewModel.isOrdering,
                        )
                    }
                }
            }
        }
    )

    when(productListViewModel.deleteProductResponse){
        is ValueOrException.Loading -> LoadingScreen()
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> Snackbar { Text(text = "Nem sikerült törölni a terméket") }
    }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            selectedProduct?.let {
                productListViewModel.onDeleteProduct(it.id.toString()){
                    Toast.makeText(context, "Termék törölve!", Toast.LENGTH_SHORT).show()
                    showDialog.value = false
                }
            }
        }
    ) {
        showDialog.value = false
    }

    selectedProduct?.let {
        FullScreenDialog(
            showDialog = showFullScreenDialog,
            selectedProduct = it,
            isKarton = null,
            currentQuantity = null,
            onAdd = { state: Boolean, quantity: String ->
                if (quantity.isDigitsOnly() && quantity!=""){
                    val orderItem = MOrderItem(
                        id = UUID.randomUUID().toString(),
                        amount = quantity.toInt(),
                        orderID = productListViewModel.orderId,
                        productID = selectedProduct!!.id.toString(),
                        statusID = 0,
                        carton = !state,
                        piece = state
                    )
                    onAddOrderItem(orderItem)
                    navigateBackToOrder(productListViewModel.orderId, productListViewModel.customerId)
                } else {
                    Toast.makeText(context, "A mennyiség megadásánál csak számokat használj!", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            showFullScreenDialog.value = false
        }
    }
}


@Composable
fun MenuBar(
    categories: List<Categ>,
    selectedCategory: Categ?,
    onCategorySelected: (Categ?) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(top = 13.dp)
    ) {
        items(categories) { category ->
            Text(
                text = category.name,
                color = if (category == selectedCategory) MaterialTheme.colors.primary else Color.Gray,
                modifier = Modifier
                    .padding(end = 30.dp, start = 16.dp)
                    .clickable(onClick = { onCategorySelected(category) })
            )
        }
    }
}

@Composable
fun ProductList(data: List<MProduct>, onDelete: (MProduct) -> Unit, onEdit: (MProduct) -> Unit, onAddToOrder: (MProduct) -> Unit, iconClickEnabled: Boolean){
    CreateList(
        data = data.sortedBy { it.title },
        onDelete = {
            onDelete(it)
       },
        onEdit = {
            onEdit(it)
        },
        onClick = {
            onAddToOrder(it)
        },
        iconClickEnabled = iconClickEnabled,
        itemContent = { product ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.size(70.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = product.image.toUri(),
                        contentDescription = "profile image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                ) {
                    Text(text = product.title, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${product.pricePerPiece}HUF / ${product.pricePerKarton}HUF",
                        style = MaterialTheme.typography.caption
                    )

                }
            }
        })
}