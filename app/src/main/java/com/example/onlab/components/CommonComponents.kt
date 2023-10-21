package com.example.onlab.components

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.onlab.PermissionRequester
import com.example.onlab.R
import com.example.onlab.model.Category
import com.example.onlab.model.MProduct
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.screens.product.ProductButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


data class BottomNavItem(val name: String, val icon: ImageVector)

@Composable
fun CreateIcon(icons: ImageVector, modifier: Modifier = Modifier, clickEnabled: Boolean = true, onClick: () -> Unit) {
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
                    enabled = clickEnabled,
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
                        "Termékek" -> navController.navigate("${ProductScreens.ListScreen.name}/false")
                        "Rendelések" -> navController.navigate("OrdersScreen")
                        "Beállítások" -> navController.navigate("ProfileScreen")
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
                        color = if (selectedItem == item) Color.White else Color.White.copy(alpha = 0.4f),
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
fun createTopBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    text: String,
    withIcon: Boolean,
    onBack: () -> Unit = { navController.popBackStack() },
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        modifier = Modifier.height(70.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (withIcon) {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Icon",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        onBack()
                    })
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(modifier = modifier, text = text, fontSize = 27.sp, fontWeight = FontWeight.Normal, color = Color.White)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun CategoryDropDownMenu(
    selectedCategory: String,
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
            value = selectedCategory,
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
fun ImagePickerButton(permissionRequester: PermissionRequester, onImageSelected: (Uri) -> Unit) {
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
    val openAlertDialog = remember {
        mutableStateOf(false)
    }
    ProductButton(modifier = Modifier
        .padding(end = 10.dp)
        .height(40.dp),
        text = "Kép hozzáadása",
        onClick = {
            permissionRequester.requestPermission(
                context = context,
                permission = android.Manifest.permission.READ_EXTERNAL_STORAGE,
                showRationale = {
                    // Show rationale before requesting permission
                    Toast.makeText(context, "We need access to your photos to proceed.", Toast.LENGTH_SHORT).show()
                },
                onPermissionDenied = {
                    // Handle permission denied
                    openAlertDialog.value = true
                    Toast.makeText(context, "You have decided not to grant access to your photos.", Toast.LENGTH_SHORT).show()
                }
            ) {
                Log.d("show", "ImagePickerButton: we need permission please")
                imagePicker.launch("image/*")
            }
        }
    )

    when {
        openAlertDialog.value -> {
            permissionRequester.NotificationPermissionRationaleDialog(
                icon = painterResource(id = R.drawable.cloud_storage),
                headline = "Tölts fel képeket!",
                strapline = "Ahhoz hogy képeket tudj feltölteni engedélyezned kell a fotók elérését.",
                image = painterResource(id = R.drawable.gallery),
                onSkip = {
                    openAlertDialog.value = false
                }
            ) {
                openAlertDialog.value = false
                val intent: Intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                startActivity(context,intent, Bundle.EMPTY)
            }
        }
    }
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
fun DismissChangesDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            icon = { Icon(Icons.Filled.Warning, contentDescription = "Warning icon") },
            title = {
                androidx.compose.material.Text(text = "Biztos kilépsz mentés nélkül?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm() // Call the onConfirm lambda
                    }
                ) {
                    androidx.compose.material.Text("Igen") // Text for the button
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    androidx.compose.material.Text("Mégsem")
                }
            }
        )

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullScreenDialog(
    showDialog: MutableState<Boolean>,
    selectedProduct: MProduct,
    currentQuantity: Int?,
    isKarton: Boolean?,
    onAdd: (state: Boolean, quantity: String) -> Unit,
    onClose: () -> Unit
){
    var state = remember {
        mutableStateOf(isKarton != true)
    }
    var value by remember {
        mutableStateOf(currentQuantity?.toString() ?: "")
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    if (showDialog.value){
        Dialog(
            onDismissRequest = onClose,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                androidx.compose.material3.TopAppBar(
                    title = { Text(text = selectedProduct.title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            showDialog.value = false
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onAdd(state.value, value.toString())
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
                
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = selectedProduct.image.toUri(),
                        contentDescription = "profile image",
                        modifier = Modifier
                            .size(200.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 20.dp, top = 20.dp),
                        text = selectedProduct.title,
                        textAlign = TextAlign.Start,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.h4
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedProduct.pricePerPiece.toString() + " HUF/db",
                            textAlign = TextAlign.Start,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.subtitle2,
                            color = MaterialTheme.colors.primary
                        )
                        Text(
                            text = selectedProduct.pricePerKarton.toString() + " HUF/karton",
                            textAlign = TextAlign.End,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.subtitle1,
                            color = Color.Gray
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        androidx.compose.material3.TextField(
                            value = value,
                            onValueChange = { newValue ->
                                value = newValue
                            },
                            maxLines = 1,
                            label = { Text(text = "Mennyiség") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        Row(Modifier.selectableGroup()) {
                            RadioButton(
                                selected = state.value,
                                onClick = {
                                    state.value = true
                                },
                                modifier = Modifier.semantics {
                                    contentDescription = "Localized Description"
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colors.primary,
                                    unselectedColor = Color.Gray
                                )
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = "Darab",
                                modifier = Modifier
                                    .clickable(onClick = { state.value = true })
                                    .align(Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            RadioButton(
                                selected = !state.value,
                                onClick = { state.value = false },
                                modifier = Modifier.semantics {
                                    contentDescription = "Localized Description"
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colors.primary,
                                    unselectedColor = Color.Gray
                                )
                            )
                            Text(
                                text = "Karton",
                                modifier = Modifier
                                    .clickable(onClick = { state.value = false })
                                    .align(Alignment.CenterVertically),
                            )
                        }
                    }
                }
            }
            
        }
    }
}



@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullScreenDidalog(
    showDialog: MutableState<Boolean>,
    selectedProduct: MProduct,
    currentQuantity: Int?,
    isKarton: Boolean?,
    onAdd: (state: Boolean, quantity: String) -> Unit,
    onClose: () -> Unit
) {
    var state = remember { mutableStateOf(isKarton != true) }
    var value by remember { mutableStateOf(currentQuantity?.toString() ?: "") }
    Log.d("TAG", "value: ${value}")
    if (showDialog.value) {
        Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.primary),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            modifier = Modifier
                                .clickable {
                                    showDialog.value = false
                                }
                                .padding(20.dp),
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                            Text(modifier = Modifier
                                .padding(20.dp)
                                .clickable {
                                    onAdd(state.value, value.toString())
                                },
                                text = "Mentés", fontSize = 22.sp, fontWeight = FontWeight.Normal, color = Color.White)
                    }
                    AsyncImage(
                        model = selectedProduct.image.toUri(),
                        contentDescription = "profile image",
                        modifier = Modifier
                            .weight(2f)
                            .padding(20.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .weight(2f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 20.dp, top = 20.dp),
                            text = selectedProduct.title,
                            textAlign = TextAlign.Start,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.h4
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedProduct.pricePerPiece.toString() + " HUF/db",
                                textAlign = TextAlign.Start,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.subtitle2,
                                color = MaterialTheme.colors.primary
                            )
                            Text(
                                text = selectedProduct.pricePerKarton.toString() + " HUF/karton",
                                textAlign = TextAlign.End,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Normal,
                                style = MaterialTheme.typography.subtitle1,
                                color = Color.Gray
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            androidx.compose.material3.TextField(
                                value = value,
                                onValueChange = { newValue ->
                                   value = newValue
                                },
                                maxLines = 1,
                                label = { Text(text = "Mennyiség") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            Row(Modifier.selectableGroup()) {
                                RadioButton(
                                    selected = state.value,
                                    onClick = {
                                        state.value = true
                                    },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Localized Description"
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colors.primary,
                                        unselectedColor = Color.Gray
                                    )
                                )
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = "Darab",
                                    modifier = Modifier
                                        .clickable(onClick = { state.value = true })
                                        .align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.width(30.dp))
                                RadioButton(
                                    selected = !state.value,
                                    onClick = { state.value = false },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Localized Description"
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colors.primary,
                                        unselectedColor = Color.Gray
                                    )
                                )
                                Text(
                                    text = "Karton",
                                    modifier = Modifier
                                        .clickable(onClick = { state.value = false })
                                        .align(Alignment.CenterVertically),
                                )
                            }

                        }

                    }


                }


            }
        }
    }
}
        @Composable
        fun <T> CreateList(
            data: List<T>,
            onDelete: (T) -> Unit,
            onEdit: (T) -> Unit,
            onClick: (T) -> Unit = {},
            iconClickEnabled: Boolean = true,
            iconContent: @Composable (item: T) -> Unit = {},
            itemContent: @Composable (item: T) -> Unit
        ) {
            LazyColumn {
                items(data) { item ->
                    Card(
                        modifier = Modifier
                            .padding(13.dp)
                            .fillMaxWidth()
                            .clickable(onClick = { onClick(item) }),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(7.dp)
                            ) {
                                itemContent(item)
                            }
                            Row(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp) // Add spacing between icons
                            ) {
                                iconContent(item)
                                CreateIcon(Icons.Rounded.Edit, clickEnabled = iconClickEnabled) {
                                    onEdit(item)
                                }
                                CreateIcon(Icons.Rounded.Delete, clickEnabled = iconClickEnabled) {
                                    onDelete(item)
                                }
                            }
                        }
                    }
                }
            }
        }


        @Composable
        fun ToggleButtons(
            items: List<String>,
            selectedIndex: Int,
            onSelectedIndexChange: (Int) -> Unit,
        ) {
            val cornerRadius = 8.dp

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                items.forEachIndexed { index, item ->
                    OutlinedButton(
                        onClick = { onSelectedIndexChange(index) },
                        shape = when {
                            // left outer button
                            index == 0 -> RoundedCornerShape(
                                topStart = cornerRadius,
                                bottomStart = cornerRadius
                            )
                            // right outer button
                            index == 1 -> RoundedCornerShape(
                                topEnd = cornerRadius,
                                bottomEnd = cornerRadius
                            )
                            // middle button
                            else -> RectangleShape
                        },
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedIndex == index) {
                                MaterialTheme.colors.primary
                            } else {
                                Color.DarkGray.copy(alpha = 0.75f)
                            }
                        ),
                        colors = if (selectedIndex == index) {
                            // selected colors
                            ButtonDefaults.outlinedButtonColors(
                                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colors.primary
                            )
                        } else {
                            // not selected colors
                            ButtonDefaults.outlinedButtonColors(
                                backgroundColor = MaterialTheme.colors.surface,
                                contentColor = MaterialTheme.colors.primary
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .zIndex(if (selectedIndex == index) 1f else 0f),
                    ) {
                        Text(
                            text = item,
                            color = if (selectedIndex == index) {
                                MaterialTheme.colors.primary
                            } else {
                                Color.DarkGray.copy(alpha = 0.9f)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }


        }






