package com.example.onlab.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlab.components.BottomNavBar
import com.example.onlab.components.CreateList
import com.example.onlab.data.ProductDataSource
import com.example.onlab.model.Product
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.model.Category as Categ

@Preview
@Composable
fun ProductListScreen() {
    val categoryList = getCategoryTypes(com.example.onlab.model.Category::class.java)
    val products: MutableList<Product> = ProductDataSource().loadProducts().toMutableList()

    var selectedCategory by remember { mutableStateOf<Categ?>(null) }

    var filteredProducts = if (selectedCategory != null) {
        products.filter { it.category == selectedCategory }
    } else {
        products
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
                onClick = {  },
                shape = RoundedCornerShape(20.dp),
                backgroundColor = MaterialTheme.colors.primary,
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = {
            it.calculateBottomPadding()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                MenuBar(
                    categories = categoryList,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category -> selectedCategory = category }
                )

                CreateList(data = filteredProducts, {
                    products.remove(it)
                }) { product ->
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


