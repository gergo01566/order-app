package com.example.onlab.screen.product

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.R
import com.example.onlab.components.BottomNavBar
import com.example.onlab.navigation.ProductScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalComposeUiApi
@Composable
fun ProductDetailsScreen(navController: NavController, productID: String? = null, productViewModel: ProductViewModel) {

    var product by remember { mutableStateOf(productViewModel.getProductById(productID!!)) }

    val showDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

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
                    product = product!!.copy(image = imageFile.toUri().toString())
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


    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Column(modifier = Modifier.padding(5.dp)) {
                    Text("Biztos törölni szeretnéd a következő terméket?")
                    //Text(text = product?.get(0)!!.title)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        productViewModel.removeProduct(product!!)
                        showDialog.value = false
                        navController.navigate(route = ProductScreens.ListScreen.name)
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
                    Text(text = product!!.title, fontSize = 27.sp, fontWeight = FontWeight.Normal)
                }
            }
        },
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController)
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
                        product = product?.copy(title = newValue) ?: product
                    },
                    label = { Text(text = "Termék neve") },
                    placeholder = { Text(text = "Add meg a termék nevét!") }
                )
                TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((10.dp)),
                value = product?.pricePerPiece.toString(),
                onValueChange = { newValue ->
                    product = product?.copy(pricePerPiece = newValue.toInt()) ?: product
                },
                label = { Text(text = "Termék ára/db") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                placeholder = { Text(text = "Add meg a termék darab árát!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = product?.pricePerKarton.toString(),
                    onValueChange = { newValue ->
                        product = product?.copy(pricePerKarton = newValue.toInt()) ?: product
                    },
                    label = { Text(text = "Termék ára/karton") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = "Add meg a termék karton árát!") }
                )
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
                    .height(40.dp),text = "Termék mentése", onClick = { productViewModel.updateProduct(
                    product!!
                ) })
                ProductButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp))
                    .height(40.dp),
                    text = "Termék törlése",
                    onClick = { showDialog.value = true},
                    color = Color.Red)
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
    color: Color = MaterialTheme.colors.primary
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


