package com.example.onlab.screen.product

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.onlab.PermissionRequester
import com.example.onlab.components.*
import com.example.onlab.model.Category
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.DestinationProductDetails
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.IOException
import java.util.*

@ExperimentalMaterialApi
@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalComposeUiApi
@Composable
fun ProductDetailsScreen(
    navigateFromTo: (String, String) -> Unit,
    navigateBack:()-> Unit,
    permissionRequester: PermissionRequester,
    viewModel: ProductDetailsViewModel= hiltViewModel()
) {

    val product by viewModel.product

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
            viewModel.onDeleteProduct(product.id!!, navigateFromTo = navigateFromTo)
//            productViewModel.deleteProduct(productID!!){
//                showDialog.value = false
//                Toast.makeText(context, "Termék törölve", Toast.LENGTH_SHORT).show()
//                navController.navigate(route = ProductScreens.ListScreen.name)
//            }
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    Scaffold(
        topBar = {
            createTopBar(text =product!!.title , withIcon = true){
                if(changesMade){
                    showAlertDialog = true
                } else {
                    navigateBack()
                }
            }
        },
        bottomBar = {
            BottomNavBar(selectedItem = items[2], navigateTo = {
                navigateFromTo(DestinationProductDetails, it)
            })
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
                BasicField(label = "Add meg a termék nevét", text = "Termék neve", value = product.title, onNewValue = { viewModel.onTitleChange(it) })
                BasicField(label = "Add meg a termék árát'", text = "Termék ára (darab)", value = product.pricePerPiece.toString(), onNewValue = { viewModel.onPricePieceChange(it) }, keyboardType = KeyboardType.Number)
                BasicField(label = "Add meg a termék árát'", text = "Termék ára (karton)", value = product.pricePerKarton.toString(), onNewValue = { viewModel.onPriceCartonChange(it) }, keyboardType = KeyboardType.Number)
                CategoryDropDownMenu(
                    selectedCategory = product.category,
                    onCategorySelected = { category ->
                        viewModel.onCategoryChange(category.toString())
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
                        viewModel.onImageChange(it.toString())
//                        imageUri = it
//                        product = product.copy(image = it.toString())
                    }, permissionRequester = permissionRequester)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        AsyncImage(
                            model = product.image.toUri(),
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
                    enabled = product.title.isNotEmpty() && product.pricePerPiece != 0 && product.pricePerKarton != 0,
                    onClick = {
                        if (product.pricePerPiece != 0 && product.pricePerKarton != 0) {
                            viewModel.onDoneClick(product, navigateFromTo)
//                            productViewModel.updateProduct(productToUpdate as Map<String, String?>, productID!!, onSuccess = {
//                                navController.navigate(route = ProductScreens.ListScreen.name)
//                                Toast.makeText(contextForToast, "Termék módosítva", Toast.LENGTH_SHORT).show()
//                            }, onFailure = {
//                                Toast.makeText(contextForToast, "Termék nem lett módosítva", Toast.LENGTH_SHORT).show()
//                            })
//                        }else {
//                            Toast.makeText(contextForToast, "Csak számokat használj az ár megadásánál", Toast.LENGTH_LONG).show()
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
                        navigateBack()
                    }
                }

                if(showAlertDialog){
                    DismissChangesDialog(onDismiss = { showAlertDialog = false }) {
                        showAlertDialog = false
                        navigateBack()
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

@Composable
fun BasicField(
    text: String,
    label: String,
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        singleLine = true,
        maxLines = 1,
        modifier = modifier.fillMaxWidth().padding(10.dp),
        value = if (value=="0") "" else value,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        label = { Text(label) },
        onValueChange = { onNewValue(it) },
        placeholder = { Text(text) }
    )
}



