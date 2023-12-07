package com.example.orderapp.screens.product

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.orderapp.common.composables.*
import com.example.orderapp.model.*
import java.util.*
import com.example.orderapp.model.Category as Categ
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.navigation.DestinationProductDetails
import com.example.orderapp.navigation.DestinationProductList
import com.example.orderapp.screens.customers.AddButton
import com.example.orderapp.screens.customers.LoadingScreen
import com.example.orderapp.screens.customers.SearchBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigate: (String) -> Unit,
    navigateFromTo: (String, String) -> Unit,
    navigateBackToOrder: (String, String) ->Unit,
    productListViewModel: ProductListViewModel = hiltViewModel(),
) {

    val showDialog = remember { mutableStateOf(false) }
    val showFullScreenDialog = remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<Categ?>(com.example.orderapp.model.Category.Összes) }

    Scaffold(
        topBar = {
            if (productListViewModel.isOrdering)
                androidx.compose.material3.TopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary),
                    title = {
                        androidx.compose.material3.Text("Rendelések", color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                    },
                )
            else
                androidx.compose.material3.TopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary),
                    title = {
                        androidx.compose.material3.Text("Termékek", color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                    },
                )
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
                is ValueOrException.Failure -> Unit
                is ValueOrException.Success -> {
                    it.calculateBottomPadding()
                    Column(
                        modifier = Modifier
                            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.surface)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = it.calculateBottomPadding())
                    ) {
                        MenuBar(
                            categories = getCategoryTypes(com.example.orderapp.model.Category::class.java),
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
                            onAddToOrder = {
                                if(productListViewModel.isOrdering) {
                                    selectedProduct = it
                                    showFullScreenDialog.value = true
                                }
                                else{
                                    onNavigate(it.id.toString())
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
        else -> Unit
    }

    ShowConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            selectedProduct?.let {
                productListViewModel.onDeleteProduct(it.id.toString()){
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
                    val orderItem = OrderItem(
                        id = UUID.randomUUID().toString(),
                        amount = quantity.toInt(),
                        orderID = productListViewModel.orderId,
                        productID = selectedProduct!!.id.toString(),
                        statusID = -1,
                        carton = !state,
                        piece = state
                    )
                    productListViewModel.onAddOrderItemLocally(orderItem)
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
fun ProductList(data: List<Product>, onDelete: (Product) -> Unit, onAddToOrder: (Product) -> Unit, iconClickEnabled: Boolean){
    CreateList(
        data = data.sortedBy { it.title },
        onClick = {
            onAddToOrder(it)
        },
        icons = listOf(
            Icons.Default.Delete to { product -> onDelete(product) },
        ),
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