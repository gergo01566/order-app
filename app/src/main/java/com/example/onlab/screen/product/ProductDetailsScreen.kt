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
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Product
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

    val uiState by viewModel.state

    var changesMade by remember { mutableStateOf(false) }

    var showAlertDialog by remember { mutableStateOf(false) }

    val showDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )


    LaunchedEffect(permissionsState) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            Log.d("not granted", "ProductDetailsScreen: NOT ALL PERMISSION GRANTED")
        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            Log.d("granted", "ProductDetailsScreen: ALL PERMISSION GRANTED")
            try {
                when(val productsResponse = viewModel.productResponse){
                    is ValueOrException.Success -> {
                        val inputStream = context.contentResolver.openInputStream(Uri.parse(
                            productsResponse.data.image))
                        loadedBitmap = BitmapFactory.decodeStream(inputStream)
                    }
                    else -> {}
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            viewModel.onDeleteProduct(uiState.id, navigateFromTo = navigateFromTo)
        },
        onDismiss = {
            showDialog.value = false
        }
    )

    when(viewModel.productResponse) {
        is ValueOrException.Loading -> CircularProgressIndicator()
        is ValueOrException.Failure -> CircularProgressIndicator()
        is ValueOrException.Success ->
            Scaffold(
                topBar = {
                    createTopBar(text = uiState.title , withIcon = true){
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
                        BasicField(label = "Add meg a termék nevét", text = "Termék neve", value = uiState.title, onNewValue = { viewModel.onTitleChange(it) })
                        BasicField(label = "Add meg a termék árát'", text = "Termék ára (darab)", value = uiState.pricePerPiece, onNewValue = { viewModel.onPricePieceChange(it) }, keyboardType = KeyboardType.Number)
                        BasicField(label = "Add meg a termék árát'", text = "Termék ára (karton)", value = uiState.pricePerCarton, onNewValue = { viewModel.onPriceCartonChange(it) }, keyboardType = KeyboardType.Number)
                        CategoryDropDownMenu(
                            selectedCategory = uiState.category,
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
                            }, permissionRequester = permissionRequester)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(15.dp)
                            ) {
                                AsyncImage(
                                    model = uiState.image.toUri(),
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
                            enabled = uiState.title.isNotEmpty() && uiState.pricePerPiece.isNotEmpty() && uiState.pricePerCarton.isNotEmpty(),
                            onClick = {
                                    viewModel.onDoneClick(Product(uiState), navigateFromTo)

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

    when(val saveProductResponse = viewModel.saveProductResponse) {
        is ValueOrException.Loading -> CircularProgressIndicator()
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> print(saveProductResponse.e)
    }

    when(val updateProductResponse = viewModel.updateProductResponse) {
        is ValueOrException.Loading -> CircularProgressIndicator()
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> print(updateProductResponse.e)
    }

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
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = if (value=="0") "" else value,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        label = { Text(label) },
        onValueChange = { onNewValue(it) },
        placeholder = { Text(text) }
    )
}



