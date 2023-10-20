package com.example.onlab.screen.product

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.PermissionRequester
import com.example.onlab.components.*
import com.example.onlab.model.Category
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.MProductViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.IOException
import java.util.*

@ExperimentalMaterialApi
@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalComposeUiApi
@Composable
fun ProductDetailsScreen(navController: NavController, productID: String? = null, productViewModel: MProductViewModel, permissionRequester: PermissionRequester) {

    var product by remember {
        mutableStateOf(productViewModel.data.value.data?.first{mProduct->
            mProduct.id == productID.toString()
        })
    }

    var buttonEnabled by remember { mutableStateOf(false) }

    var changesMade by remember { mutableStateOf(false) }

    var showAlertDialog by remember { mutableStateOf(false) }

    val listItems = getCategoryTypes(Category::class.java)

    var selectedItem by remember {
        mutableStateOf(listItems[0])
    }

    val showDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    var pricePerPiece by remember {
        mutableStateOf(product!!.pricePerPiece.toString())
    }

    var pricePerKarton by remember {
        mutableStateOf(product!!.pricePerKarton.toString())
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(product!!.image.toUri())
    }

    val contextForToast = LocalContext.current.applicationContext


    LaunchedEffect(permissionsState) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            Log.d("not granted", "ProductDetailsScreen: NOT ALL PERMISSION GRANTED")
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            Log.d("granted", "ProductDetailsScreen: ALL PERMISSION GRANTED")
            try {
                val inputStream = context.contentResolver.openInputStream(Uri.parse(product!!.image))
                loadedBitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            productViewModel.deleteProduct(productID!!){
                showDialog.value = false
                Toast.makeText(context, "Termék törölve", Toast.LENGTH_SHORT).show()
                navController.navigate(route = ProductScreens.ListScreen.name)
            }
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    Scaffold(
        topBar = {
            createTopBar(navController = navController, text =product!!.title , withIcon = true){
                if(changesMade){
                    showAlertDialog = true
                } else {
                    navController.popBackStack()
                }
            }
        },
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController, selectedItem = items[2])
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
                    value = product?.title ?: "",
                    onValueChange = { newValue ->
                        if(newValue.length <= 20){
                            product = product?.copy(title = newValue) ?: product
                            changesMade = true
                        } else {
                            Toast.makeText(
                                contextForToast,
                                "A termék neve max. 20 karakter lehet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    label = { Text(text = "Termék neve") },
                    placeholder = { Text(text = "Add meg a termék nevét!") }
                )
                TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((10.dp)),
                value = pricePerPiece,
                onValueChange = { newValue ->
                    pricePerPiece = newValue
                    product = product!!.copy(pricePerPiece = pricePerPiece.toIntOrNull() ?: 0)
                    changesMade = true
                },
                label = { Text(text = "Termék ára/db") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                placeholder = { Text(text = "Add meg a termék darab árát!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = pricePerKarton,
                    onValueChange = { newValue ->
                        pricePerKarton = newValue
                        product = product!!.copy(pricePerKarton = pricePerKarton.toIntOrNull() ?: 0)
                        changesMade = true
                    },
                    label = { Text(text = "Termék ára/karton") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = "Add meg a termék karton árát!") }
                )
                CategoryDropDownMenu(
                    selectedCategory = product!!.category,
                    onCategorySelected = { category ->
                        selectedItem = category
                        product = product!!.copy(category = category.toString())
                        changesMade = true
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                        .height(150.dp)
                ) {
                    ImagePickerButton(onImageSelected = {
                        imageUri = it
                        product = product!!.copy(image = it.toString())
                    }, permissionRequester = permissionRequester)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
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
                    .height(40.dp),
                    text = "Termék mentése",
                    enabled = product!!.title.isNotEmpty() && product!!.pricePerPiece != 0 && product!!.pricePerKarton != 0,
                    onClick = {
                        if (product!!.pricePerPiece != 0 && product!!.pricePerKarton != 0) {
                            val productToUpdate = hashMapOf(
                                "product_title" to product?.title,
                                "product_category" to product?.category,
                                "price_piece" to product?.pricePerPiece,
                                "price_carton" to product?.pricePerKarton,
                                "product_image" to product?.image
                            ).toMap()
                            productViewModel.updateProduct(productToUpdate as Map<String, String?>, productID!!, onSuccess = {
                                navController.navigate(route = ProductScreens.ListScreen.name)
                                Toast.makeText(contextForToast, "Termék módosítva", Toast.LENGTH_SHORT).show()
                            }, onFailure = {
                                Toast.makeText(contextForToast, "Termék nem lett módosítva", Toast.LENGTH_SHORT).show()
                            })
                        }else {
                            Toast.makeText(contextForToast, "Csak számokat használj az ár megadásánál", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                ProductButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp))
                    .height(40.dp),
                    text = "Termék törlése",
                    onClick = { showDialog.value = true},
                    color = Color.Red,
                )

                BackHandler{
                    if (changesMade){
                        showAlertDialog = true
                    } else {
                        navController.popBackStack()
                    }
                }

                if(showAlertDialog){
                    DismissChangesDialog(onDismiss = { showAlertDialog = false }) {
                        showAlertDialog = false
                        navController.popBackStack()
                    }
                }
            }
        }
    )
}

@Composable
fun ProductButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colors.primary,
){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(15.dp),
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = color)
    ) {
        Text(color = Color.White, text = text)

    }
}


