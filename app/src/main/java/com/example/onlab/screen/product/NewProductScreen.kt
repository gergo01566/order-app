@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.onlab.screen.product
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.onlab.components.BottomNavBar
import com.example.onlab.model.Category
import com.example.onlab.model.Product
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.google.accompanist.permissions.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun NewProductScreen(navController: NavController, productViewModel: ProductViewModel) {
    val listItems = getCategoryTypes(Category::class.java)
    val contextForToast = LocalContext.current.applicationContext

    // state of the menu
    var expanded by remember {
        mutableStateOf(false)
    }

    // remember the selected item
    var selectedItem by remember {
        mutableStateOf(listItems[0])
    }


    var product by remember { mutableStateOf(Product(title = "", pricePerPiece = 0, pricePerKarton = 0,category = Category.Italok)) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    var hasImage by remember {
        mutableStateOf(false)
    }
    // 2
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

//    val imagePicker = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri ->
//            // 3
//            hasImage = uri != null
//            imageUri = uri
//            product = product.copy(image = imageUri.toString())
//        }
//    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                val inputStream = contextForToast.contentResolver.openInputStream(uri)
                var outputStream: OutputStream? = null
                try {
                    // Create a file in app-specific storage directory
                    val uniqueId = UUID.randomUUID().toString()
                    val imageFile = File(contextForToast.filesDir, "image_$uniqueId.jpg")
                    outputStream = FileOutputStream(imageFile)

                    // Copy the selected image to the file
                    inputStream?.copyTo(outputStream)

                    // Update the product object with the file URI
                    product = product.copy(image = imageFile.toUri().toString())
                    imageUri = imageFile.toUri()

                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            }
        }
    )



    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MyPermission(
        multiplePermissionState: MultiplePermissionsState,
    ) {
        PermissionsRequired(
            multiplePermissionsState = multiplePermissionState,
            permissionsNotGrantedContent = {
                Toast.makeText(contextForToast, "Permissions not granted", Toast.LENGTH_SHORT).show()
                                           },
            permissionsNotAvailableContent = {
                Toast.makeText(contextForToast, "Permissions not available", Toast.LENGTH_SHORT).show()
            }
        ) {
        }
    }

    MyPermission(multiplePermissionState = permissionsState)

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                modifier = Modifier.height(70.dp)
            ) {
                Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow Back",
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        })
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(text = "Új termék", fontSize = 27.sp, fontWeight = FontWeight.Normal)
                }
            }
        },
        bottomBar = {
            BottomNavBar()
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .padding(bottom = padding.calculateBottomPadding() / 2),
                horizontalAlignment = Alignment.Start
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = product.title,
                    onValueChange = { newValue ->
                        product = product.copy(title = newValue)
                    },
                    label = { Text(text = "Termék neve") },
                    placeholder = { Text(text = "Add meg a termék nevét!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = product.pricePerPiece.toString(),
                    onValueChange = { newValue ->
                        product = product.copy(pricePerPiece = newValue.toInt())
                    },
                    label = { Text(text = "Termék ára/db") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = "Add meg a termék darab árát!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = product.pricePerKarton.toString(),
                    onValueChange = { newValue ->
                        product = product.copy(pricePerKarton = newValue.toInt())
                    },
                    label = { Text(text = "Termék ára/karton") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = "Add meg a termék karton árát!") }
                )
                // category type drop-down list
                // box
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    // text field
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = selectedItem.toString(),
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

                    // menu
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // this is a column scope
                        // all the items are added vertically
                        listItems.forEach { selectedOption ->
                            // menu item
                            DropdownMenuItem(onClick = {
                                selectedItem = selectedOption
                                product = product.copy(category = selectedItem)
                                expanded = false
                            }) {
                                Text(text = selectedOption.toString())
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                        .height(150.dp)
                ) {
                    ProductButton(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .height(40.dp),
                        text = "Kép hozzáadása",
                        onClick = {
                            permissionsState.launchMultiplePermissionRequest()
                            imagePicker.launch("image/*")
                        }
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "profile image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                    }
                }

                ProductButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp))
                    .height(40.dp),text = "Termék mentése", onClick = {
                    Toast.makeText(contextForToast, "Termék hozzáadva", Toast.LENGTH_SHORT).show()
                    productViewModel.addProduct(product = product)
                    navController.navigate(route = ProductScreens.ListScreen.name)
                    })
            }
        }
    )
}








