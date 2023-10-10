@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.onlab.screen.product
import android.net.Uri
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.model.Category
import com.example.onlab.model.MProduct
import com.example.onlab.model.getCategoryTypes
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.MProductViewModel
import com.google.accompanist.permissions.*
import java.io.*
import java.util.*


@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun NewProductScreen(navController: NavController, productViewModel: MProductViewModel) {

    val listItems = getCategoryTypes(Category::class.java)

    val contextForToast = LocalContext.current.applicationContext

    var selectedItem by remember {
        mutableStateOf(listItems[0])
    }

    var buttonEnabled by remember { mutableStateOf(false) }

    var product by remember { mutableStateOf(MProduct(title = "", category = "", pricePerPiece = 0, pricePerKarton = 0, image = "")) }

    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var pricePerPiece by remember {
        mutableStateOf("null")
    }

    var pricePerKarton by remember {
        mutableStateOf("null")
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MyPermission(
        multiplePermissionState: MultiplePermissionsState,
    ) {
        PermissionsRequired(
            multiplePermissionsState = multiplePermissionState,
            permissionsNotGrantedContent = {
                Toast.makeText(contextForToast, "Permissions not granted", Toast.LENGTH_SHORT).show() },
            permissionsNotAvailableContent = {
                Toast.makeText(contextForToast, "Permissions not available", Toast.LENGTH_SHORT).show()
            }
        ) {
        }
    }

    MyPermission(multiplePermissionState = permissionsState)

    Scaffold(
        topBar = {
            createTopBar(navController = navController, text = "Új termék", withIcon = true){
                if(buttonEnabled){
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
                    //value = if (product.pricePerPiece == 0) "" else product.pricePerPiece.toString(),
                    value = if (pricePerPiece == "null") "" else pricePerPiece,
                    onValueChange = { newValue ->
                        pricePerPiece = newValue
                        product = product.copy(pricePerPiece = pricePerPiece.toIntOrNull() ?: 0)
                    },
                    label = { Text(text = "Termék ára/db") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = "Add meg a termék darab árát!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = if (pricePerKarton == "null") "" else pricePerKarton,
                    maxLines = 1,
                    onValueChange = { newValue ->
                        pricePerKarton = newValue
                        product = product.copy(pricePerKarton = pricePerKarton.toIntOrNull() ?: 0)
                    },
                    label = { Text(text = "Termék ára/karton") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = "Add meg a termék karton árát!") }
                )

                CategoryDropDownMenu(
                    selectedCategory = selectedItem.toString(),
                    onCategorySelected = { category ->
                        selectedItem = category
                        product = product.copy(category = category.toString())
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
                    })
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

                buttonEnabled = !product.title.isNullOrEmpty() && product.pricePerKarton != 0 && product.pricePerKarton != null && product.pricePerPiece != 0 && product.pricePerKarton != null

                ProductButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp))
                    .height(40.dp), enabled = buttonEnabled, text = "Termék mentése", onClick = {
                    if(product.pricePerPiece != 0 && product.pricePerKarton != 0){
                        val mProduct = MProduct(title = product.title, category = product.category.toString(), pricePerPiece = product.pricePerPiece, pricePerKarton = product.pricePerKarton, image = product.image)
                        productViewModel.saveProductToFirebase(mProduct,{
                            Toast.makeText(contextForToast, "Termék hozzáadva", Toast.LENGTH_SHORT).show()
                            productViewModel.getAllProductsFromDB()
                            navController.navigate(route = ProductScreens.ListScreen.name)
                        })
                    } else {
                        Toast.makeText(contextForToast, "Csak számokat használj az ár megadásánál", Toast.LENGTH_LONG).show()
                    }

                    })

                BackHandler {
                    if(buttonEnabled){
                        showAlertDialog = true
                    } else {
                        navController.popBackStack()
                    }
                }

                if(showAlertDialog){
                    DismissChangesDialog(onDismiss = { showAlertDialog = false }) {
                        navController.popBackStack()
                        showAlertDialog = false
                    }
                }
            }
        }
    )
}









