package com.example.onlab.screen.product

import com.example.onlab.screen.profile.ProfileImage
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlab.components.*
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Product
import com.example.onlab.screen.customer.LoadingScreen
import com.example.onlab.screen.customer.ValidationUtils
import java.util.*

@ExperimentalMaterialApi
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
fun ProductDetailsScreen(
    navigateFromTo: (String, String) -> Unit,
    navigateBack:() -> Unit,
    viewModel: ProductDetailsViewModel= hiltViewModel()
) {
    val uiState by viewModel.state
    var showAlertDialog by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onImageChange(uri.toString())
        }
    )

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Termék szerkesztése") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(
                        onClick = {
                            viewModel.onNavigateBack({
                                showAlertDialog = true
                            }){
                                navigateBack()
                            }
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.IconButton(
                        enabled = viewModel.isValidProductInputs(),
                        onClick = {
                            viewModel.onDoneClick(Product(uiState), navigateFromTo)
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Done,
                            contentDescription = "Save"
                        )
                    }
                    androidx.compose.material3.IconButton(
                        onClick = {
                            showDialog.value = true
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .fillMaxHeight()
                .statusBarsPadding()
                .padding(bottom = paddingValue.calculateBottomPadding() / 2),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(paddingValue.calculateTopPadding()))
            ProfileImage(uiState.image.toUri()) {
                singlePhotoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
            ProductData(
                productResponse = viewModel.productResponse,
                uiState = uiState,
                onTitleChange = { viewModel.onTitleChange(it) },
                onPricePieceChange = { viewModel.onPricePieceChange(it) },
                onPriceCartonChange = { viewModel.onPriceCartonChange(it) },
                onCategoryChange = { viewModel.onCategoryChange(it) }
            )
            UpdateProduct(apiResponse = viewModel.updateProductResponse)
            SaveProduct(apiResponse = viewModel.saveProductResponse)
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

        BackHandler {
            viewModel.onNavigateBack({
                showAlertDialog = true
            }){
                navigateBack()
            }
        }

        if (showAlertDialog) {
            DismissChangesDialog(onDismiss = { showAlertDialog = false }) {
                showAlertDialog = false
                navigateBack()
            }
        }
    }

}

@Composable
fun SaveProduct(
    apiResponse: ValueOrException<Boolean>
){
    when (apiResponse) {
        is ValueOrException.Loading -> LoadingScreen()
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> print(apiResponse.e)
    }
}

@Composable
fun UpdateProduct(
    apiResponse: ValueOrException<Boolean>
){
    when (apiResponse) {
        is ValueOrException.Loading -> LoadingScreen()
        is ValueOrException.Success -> Unit
        is ValueOrException.Failure -> print(apiResponse.e)
    }
}

@Composable
fun ProductButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colors.primary,
) {
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductData(
    productResponse: ValueOrException<Product>,
    uiState: ProductUiState,
    onTitleChange:(String) -> Unit,
    onPricePieceChange:(String) -> Unit,
    onPriceCartonChange:(String) -> Unit,
    onCategoryChange:(String) -> Unit,
){
    when (productResponse) {
        is ValueOrException.Loading -> LoadingScreen()
        is ValueOrException.Failure -> LoadingScreen()
        is ValueOrException.Success -> {
            BasicField(
                label = "Add meg a termék nevét!",
                text = "Termék neve",
                value = uiState.title,
                isError = !ValidationUtils.inputIsNotEmpty(uiState.title),
                onNewValue = { onTitleChange(it) })

            BasicField(
                label = "Add meg a termék árát!",
                text = "Termék ára (darab)",
                value = uiState.pricePerPiece,
                isError = !ValidationUtils.inputContaintsOnlyNumbers(uiState.pricePerPiece) || !ValidationUtils.inputIsNotEmpty(uiState.pricePerPiece),
                onNewValue = { onPricePieceChange(it) },
                keyboardType = KeyboardType.Number
            )
            BasicField(
                label = "Add meg a termék árát!",
                text = "Termék ára (karton)",
                value = uiState.pricePerCarton,
                isError = !ValidationUtils.inputContaintsOnlyNumbers(uiState.pricePerCarton) || !ValidationUtils.inputIsNotEmpty(uiState.pricePerCarton),
                onNewValue = { onPriceCartonChange(it) },
                keyboardType = KeyboardType.Number
            )
            CategoryDropDownMenu(
                selectedCategory = uiState.category,
                onCategorySelected = { category ->
                    onCategoryChange(category.toString())
                }
            )
        }
    }

}

@Composable
fun BasicField(
    text: String,
    label: String,
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false
) {
    OutlinedTextField(
        isError = isError,
        singleLine = true,
        maxLines = 1,
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = if (value == "0") "" else value,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        label = { Text(label) },
        onValueChange = { onNewValue(it) },
        placeholder = { Text(text) }
    )
}




