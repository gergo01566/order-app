package com.example.onlab.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.onlab.R
import com.example.onlab.navigation.ProductScreens

data class BottomNavItem(val name: String, val icon: ImageVector)

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


@Composable
fun BottomNavBar(navController: NavController){
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
                onClick = {
                    if (item.name == "Ügyfelek") {
                        navController.navigate("CustomerScreen") // navigate to CustomerScreen
                    } else {
                        Log.d("TAG", "BottomNavBar: Clicked")
                    }
                    if (item.name == "Rendelések") {
                        navController.navigate(ProductScreens.ListScreen.name) // navigate to CustomerScreen
                    } else {
                        Log.d("TAG", "BottomNavBar: Clicked")
                    }
                },
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
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.picture_placeholder),
            contentDescription = "profile image",
            modifier = Modifier.size(13.dp), contentScale = ContentScale.Crop
        )

    }
}

@Composable
fun createTopBar(modifier: Modifier = Modifier, navController: NavController, text: String, withIcon: Boolean){
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        modifier = Modifier.height(70.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            if (withIcon){
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Icon",
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    })
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(modifier = modifier, text = text, fontSize = 27.sp, fontWeight = FontWeight.Normal)
        }
    }
}

@Composable
fun <T> CreateList(
    data: List<T>,
    onDelete: (T) -> Unit,
    onEdit: (T) -> Unit,
    itemContent: @Composable (item: T) -> Unit
) {
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
                    CreatePicture(modifier = Modifier.size(60.dp).padding(3.dp))
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
                            onEdit(item)
                            Log.d("TAG", "Edit Icon Clicked on ${item.toString()}")
                        }
                        CreateIcon(Icons.Rounded.Delete){
                            onDelete(item)
                            Log.d("TAG", "Delete Icon Clicked on ${item.toString()}")
                        }
                    }

                }
            }
        }
    }
}
