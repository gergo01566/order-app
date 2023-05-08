package com.example.onlab.screen

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.model.OrderItem
import com.example.onlab.model.Product
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.CustomerViewModel
import com.example.onlab.viewModels.OrderItemViewModel
import com.example.onlab.viewModels.ProductViewModel
import java.util.*
import com.example.onlab.model.Category as Categ

@Composable
fun ProductListScreen(navController: NavController, orderID: UUID? = null, list: Boolean? = null, productViewModel: ProductViewModel, customerViewModel: CustomerViewModel, orderItemViewModel: OrderItemViewModel) {
    val categoryList = getCategoryTypes(com.example.onlab.model.Category::class.java)
    var selectedCategory by remember { mutableStateOf<Categ?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val showFullScreenDialog = remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val products = if (selectedCategory != null) {
        productViewModel.getProductsByCategory(selectedCategory.toString())
    } else {
        productViewModel.productList.collectAsState().value
    }

    val searchText by productViewModel.searchText.collectAsState()

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            selectedProduct?.let {
                productViewModel.removeProduct(it)
            }
            showDialog.value = false }
    ) {
        showDialog.value = false
    }

    selectedProduct?.let {
        FullScreenDialog(
            showDialog = showFullScreenDialog,
            selectedProduct = it,
            isKarton = null,
            currentQuantity = null,
            onAdd = { state: Boolean, quantity: Int ->
                val orderItem = OrderItem(
                    amount = quantity,
                    orderID = orderID!!,
                    productID = selectedProduct!!.id,
                    statusID = 0,
                    karton = !state,
                    db = state
                )
                orderItemViewModel.addOrderItem(orderItem)
                navController.popBackStack()
            }
        ) {
            showFullScreenDialog.value = false
        }
    }

    Scaffold(
        topBar = {
            if (list == true ) createTopBar(navController = navController, text = "Új rendelés", withIcon = true)
            else {
                createTopBar(navController = navController, text = "Termékek", withIcon = false)            }

        },
        bottomBar = {
            if (list == true ) BottomNavBar(navController = navController as NavHostController, selectedItem = items[1])
            else {
                BottomNavBar(navController = navController as NavHostController, selectedItem = items[2])
            }
        },
        floatingActionButton = {
            if(list == true){
                ExtendedFloatingActionButton(
                    modifier =  Modifier.padding(bottom = 60.dp),
                    text = { Text(text = "Rendelés rögzítése") },
                    onClick = {
                        //navController.navigate(route = ProductScreens.NewProductScreen.name)
                    },
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = MaterialTheme.colors.primary,
                )
            }
            ExtendedFloatingActionButton(
                modifier =  Modifier.padding(bottom = 60.dp),
                text = { Text(text = "Új termék") },
                onClick = {
                    navController.navigate(route = ProductScreens.NewProductScreen.name)
                },
                shape = RoundedCornerShape(20.dp),
                backgroundColor = MaterialTheme.colors.primary,
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = { it ->
            it.calculateBottomPadding()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = it.calculateBottomPadding())
            ) {
                MenuBar(
                    categories = categoryList,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category -> selectedCategory = category }
                )

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = searchText,
                        onValueChange = productViewModel::onSearchTextChange,
                        placeholder = { Text(text = "Keresés")})
                }

                CreateList(
                    data = products,
                    onDelete = {
                    Log.d("TAG", "ProductListScreen: ${it.id}")
                    showDialog.value = true
                    selectedProduct = it },
                    onEdit = {
                    navController.navigate(route = ProductScreens.NewProductScreen.name+"/${it.id}") },
                    onClick = {
                        if(list == true) {
                            selectedProduct = it
                            showFullScreenDialog.value = true
                        }
                        else{
                            Log.d("TAG", "ProductListScreen: ez nem jott ossze")
                        }
                    }, itemContent = { product ->
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
        }
    )
}


@Composable
fun MenuBar(
    categories: List<Categ>,
    selectedCategory: Categ?,
    onCategorySelected: (Categ?) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(13.dp)
    ) {
        item {
            Text(
                text = "Összes",
                color = if (selectedCategory == null) MaterialTheme.colors.primary else Color.Gray,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = { onCategorySelected(null) })
            )
        }

        items(categories) { category ->
            Text(
                text = category.name,
                color = if (category == selectedCategory) MaterialTheme.colors.primary else Color.Gray,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = { onCategorySelected(category) })
            )
        }
    }
}





