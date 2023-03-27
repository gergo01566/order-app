package com.example.onlab.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.onlab.R
import com.example.onlab.model.Product
import com.example.onlab.navigation.AppScreens

data class BottomNavItem(val name: String, val icon: ImageVector)

@Composable
fun CreateTopBar(topBarName: String){
    Column(modifier = Modifier.padding(top = 10.dp).height(70.dp)) {
    }
        TopAppBar(title = {
            Text(text = topBarName, fontWeight = FontWeight.Bold, fontSize = 40.sp)
        }, backgroundColor = Color.Transparent, elevation = 0.dp)
}

@Composable
private fun CreateIcon(icons: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .padding(start = 15.dp),
        shape = CircleShape,

        ) {
        Icon(
            icons,
            contentDescription = "Icon",
            modifier = modifier
                .size(11.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        onClick()
                    }
                )
        )
    }
}

@Preview
@Composable
fun BottomNavBar(){
    val items = listOf(
        BottomNavItem("Rendelések", Icons.Default.Done),
        BottomNavItem("Ügyfelek", Icons.Default.Person),
        BottomNavItem("Termékek", Icons.Default.ShoppingCart),
        BottomNavItem("Beállítások", Icons.Default.Settings)
    )
    BottomNavigation(modifier = Modifier.height(70.dp), backgroundColor = MaterialTheme.colors.primary) {
        items.forEach{ item ->
            BottomNavigationItem(
                selected = false,
                onClick = { Log.d("TAG", "BottomNavBar: Clicked") },
                icon = { Icon(modifier = Modifier.padding(bottom = 8.dp).size(30.dp), imageVector = item.icon, contentDescription = "Bottom Nav Icon")},
                label = { Text(text = item.name, fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))},
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
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
fun CreateList(data: List<Product> ,onDelete: (Product) -> Unit, itemContent: @Composable (item: Product) -> Unit) {
    LazyColumn {
        items(data) { item ->
            Card(
                modifier = Modifier
                    .padding(13.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    CreatePicture()
                    Column(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(7.dp)
                            .align(alignment = Alignment.CenterVertically)
                    ) {
                        itemContent(item)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        CreateIcon(Icons.Rounded.Edit){
                            Log.d("TAG", "Edit Icon Clicked on ${item.title}")
                        }
                        CreateIcon(Icons.Rounded.Delete){
                            onDelete(item)
                            Log.d("TAG", "Delete Icon Clicked on ${item.title}")
                        }
                    }

                }
            }
        }
    }
}
