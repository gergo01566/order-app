package com.example.onlab_2023

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.onlab_2023.ui.theme.Onlab2023Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Onlab2023Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProductsPage()
                }
            }
        }
    }
}

@Composable
fun ProductsPage() {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Column(modifier = Modifier.height(200.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
            Text(modifier = Modifier.padding(13.dp),
                text = "Termékek",
                style = MaterialTheme.typography.h1,
                fontSize = 50.sp,
                textAlign = TextAlign.Start
            )

            ExtendedFloatingActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(13.dp),
                text = { Text(text = "Add new Product") },
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(20.dp),
                backgroundColor = MaterialTheme.colors.primary
            )

            val menuItems = listOf("Category 1", "Category 2", "Category 3", "Category 4")
            var selectedMenuItem by remember { mutableStateOf("Home") }

            MenuBar(menuItems = menuItems, selectedMenuItem = selectedMenuItem) { menuItem ->
                selectedMenuItem = menuItem
            }

            CreateList(data = listOf("Product 1", "Product 2", "Product 3","Product 4", "Product 5", "Product 6", "Product 7", "Product 8"))

            //TODO
            //val navController = rememberNavController(navigators = )
        }

    }
}

@Composable
fun CreateList(data: List<String>) {
    LazyColumn {
        items(data) { item ->
            Card(modifier = Modifier
                .padding(13.dp)
                .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)

            ){
                Row(modifier = Modifier
                    .padding(8.dp)
                    .padding(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    CreatePicture()
                    Column(modifier = Modifier
                        .padding(7.dp)
                        .align(alignment = Alignment.CenterVertically)) {
                        Text(text = item, fontWeight = FontWeight.Bold)
                        Text(text = "asd", style = MaterialTheme.typography.body2)
                    }
                    Spacer(modifier = Modifier.width(width = 100.dp))
                    CreateIcon(Icons.Rounded.Edit)
                    CreateIcon(Icons.Rounded.Delete)
                }
            }
        }
    }
}

@Composable
fun MenuBar(menuItems: List<String>, selectedMenuItem: String, onMenuItemSelected: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(13.dp)
    ) {
        items(menuItems) { menuItem ->
            Text(
                text = menuItem,
                color = if (menuItem == selectedMenuItem) MaterialTheme.colors.primary else Color.Gray,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = { onMenuItemSelected(menuItem) })
            )
        }
    }
}

@Composable
fun CreatePicture(modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .size(60.dp)
            .padding(5.dp),
        shape = CircleShape

    ) {
        Image(
            painter = painterResource(id = R.drawable.picture_placeholder),
            contentDescription = "profile image",
            modifier = Modifier.size(13.dp), contentScale = ContentScale.Crop
        )

    }
}

@Composable
private fun CreateIcon(icons: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .padding(start = 15.dp),
        shape = CircleShape,

    ) {
        Icon(
            icons,
            contentDescription = "Edit",
            modifier = modifier
                .size(11.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        Log.d("TAG", "Edit icon clicked")
                    }
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Onlab2023Theme {
        ProductsPage()
    }
}