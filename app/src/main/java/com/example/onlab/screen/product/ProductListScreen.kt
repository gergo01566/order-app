package com.example.onlab.screen

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onlab.components.BottomNavBar
import com.example.onlab.components.CreateList
import com.example.onlab.data.ProductDataSource
import com.example.onlab.model.Product
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.screen.product.NewProductScreen
import com.example.onlab.screen.product.ProductViewModel
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.IOException
import javax.sql.DataSource
import com.example.onlab.model.Category as Categ

@Composable
fun ProductListScreen(navController: NavController, productViewModel: ProductViewModel) {
    val categoryList = getCategoryTypes(com.example.onlab.model.Category::class.java)
    var selectedCategory by remember { mutableStateOf<Categ?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Column(modifier = Modifier.padding(5.dp)) {
                    Text("Biztos törölni szeretnéd a következő terméket?")
                    Text(text = selectedProduct!!.title)}
                },
            confirmButton = {
                Button(
                    onClick = {
                        selectedProduct?.let { productViewModel.removeProduct(it) }
                        showDialog.value = false
                    }
                ) {
                    Text("Igen")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text("Nem")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Termékek", fontSize = 37.sp, fontWeight = FontWeight.Bold) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                modifier = Modifier.height(70.dp)
            )
        },
        bottomBar = {
            BottomNavBar()
        },
        floatingActionButton = {
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

                val products = if (selectedCategory != null) {
                    productViewModel.getProductsByCategory(selectedCategory.toString())
                } else {
                    productViewModel.productList.collectAsState().value
                }

                CreateList(data = products, {
                    Log.d("TAG", "ProductListScreen: ${it.id}")
                    showDialog.value = true
                    selectedProduct = it
                }, {
                    navController.navigate(route = ProductScreens.NewProductScreen.name+"/${it.id.toString()}")
                }
                ) { product ->
                    Text(text = product.title, fontWeight = FontWeight.Bold)
                    Text(text = "${product.pricePerPiece}HUF / ${product.pricePerKarton}HUF", style = MaterialTheme.typography.caption)
                }
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



