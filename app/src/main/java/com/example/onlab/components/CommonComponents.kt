package com.example.onlab.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.onlab.R
import com.example.onlab.model.Category
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.screen.product.ProductButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

data class BottomNavItem(val name: String, val icon: ImageVector)

@Composable
fun CreateIcon(icons: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
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

val items = listOf(
    BottomNavItem("Rendelések", Icons.Default.Done),
    BottomNavItem("Ügyfelek", Icons.Default.Person),
    BottomNavItem("Termékek", Icons.Default.ShoppingCart),
    BottomNavItem("Beállítások", Icons.Default.Settings)
)


@Composable
fun BottomNavBar(
    navController: NavController,
    selectedItem: BottomNavItem,
) {
    BottomNavigation(
        modifier = Modifier.height(70.dp),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                selected = selectedItem == item,
                onClick = {
                    when (item.name) {
                        "Ügyfelek" -> navController.navigate("CustomerScreen")
                        "Termékek" -> navController.navigate(ProductScreens.ListScreen.name)
                        else -> Log.d("TAG", "BottomNavBar: Clicked")
                    }
                },
                icon = {
                    Icon(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .size(30.dp),
                        imageVector = item.icon,
                        contentDescription = "Bottom Nav Icon",
                        tint = if (selectedItem == item) Color.White else Color.White.copy(alpha = 0.4f)
                    )
                },
                label = {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = if (selectedItem == item) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                },
                alwaysShowLabel = true
            )
        }
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
@ExperimentalMaterialApi
@Composable
fun CategoryDropDownMenu(
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectedCategory.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Kategória") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            getCategoryTypes(Category::class.java).forEach { category ->
                DropdownMenuItem(onClick = {
                    onCategorySelected(category)
                    expanded = false
                }) {
                    Text(text = category.toString())
                }
            }
        }
    }
}

@Composable
fun ImagePickerButton(onImageSelected: (Uri) -> Unit) {
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
                var outputStream: OutputStream? = null
                try {
                    // Create a file in app-specific storage directory
                    val uniqueId = UUID.randomUUID().toString()
                    val imageFile = File(context.filesDir, "image_$uniqueId.jpg")
                    outputStream = FileOutputStream(imageFile)

                    // Copy the selected image to the file
                    inputStream?.copyTo(outputStream)

                    // Call the onImageSelected callback with the file URI
                    onImageSelected(imageFile.toUri())

                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            }
        }
    )
    ProductButton(modifier = Modifier.padding(end = 10.dp).height(40.dp),
        text = "Kép hozzáadása",
        onClick = {
            imagePicker.launch("image/*")
        }
    )
}

@Composable
fun showConfirmationDialog(
    showDialog: MutableState<Boolean>,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Column(modifier = Modifier.padding(5.dp)) {
                    Text(message)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        showDialog.value = false
                    }
                ) {
                    Text("Igen")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDismiss()
                        showDialog.value = false
                    }
                ) {
                    Text("Nem")
                }
            }
        )
    }
}


@Composable
fun <T> CreateList(
    data: List<T>,
    onDelete: (T) -> Unit,
    onEdit: (T) -> Unit,
    iconContent: @Composable (item: T) -> Unit = {},
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

                    //CreatePicture(modifier = Modifier.size(60.dp).padding(3.dp))
                    Column(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(7.dp)
                            .align(alignment = Alignment.CenterVertically)
                    ) {
                        itemContent(item)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        iconContent(item)
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
