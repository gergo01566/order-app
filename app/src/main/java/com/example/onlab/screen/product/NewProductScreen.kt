package com.example.onlab.screen.product

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onlab.R
import com.example.onlab.components.BottomNavBar
import com.example.onlab.navigation.AppNavigation
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.ui.theme.OnlabTheme
import java.util.*

@ExperimentalComposeUiApi
@Composable
fun NewProductScreen(navController: NavController, productID: String? = null, productViewModel: ProductViewModel = viewModel()) {
    var value by remember {
        mutableStateOf("")
    }
    val showDialog = remember { mutableStateOf(false) }
    val product = if (productID == null) null else ProductViewModel().getAllProduct().filter { product -> product.title == productID}

    Log.d("TAG", "NewProductScreen: ")
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Column(modifier = Modifier.padding(5.dp)) {
                    Text("Biztos törölni szeretnéd a következő terméket?")
                    Text(text = product!![0].title)}
            },
            confirmButton = {
                Button(
                    onClick = {
                        product!![0].title?.let { ProductViewModel().removeProduct(product[0]) }
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

                    value = if (product != null) product!![0].title else "",
                    onValueChange = { newValue ->
                        value = newValue;
                    },
                    label = { Text(text = "Termék neve") },
                    placeholder = { Text(text = "Add meg a termék nevét!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = if (product != null) product!![0].pricePerPiece.toString() else value,
                    onValueChange = { newValue ->
                        value = newValue;
                    },
                    label = { Text(text = "Termék ára/db") },
                    placeholder = { Text(text = "Add meg a termék darab árát!") }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((10.dp)),
                    value = if (product != null) product!![0].pricePerKarton.toString() else value,
                    onValueChange = { newValue ->
                        value = newValue;
                    },
                    label = { Text(text = "Termék ára/karton") },
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
                        onClick = { /*TODO*/ }
                    )
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.picture_placeholder),
                            contentDescription = "profile image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                ProductButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding((10.dp))
                    .height(40.dp),text = "Termék mentése", onClick = { /*TODO*/ })
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

@Composable
fun OutLineTextFieldSample(modifier: Modifier = Modifier, text: String, label: String) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    OutlinedTextField(
        modifier = modifier,
        value = text,
        label = { Text(text = label) },
        onValueChange = {
            text = it
        }
    )
}

@ExperimentalComposeUiApi
@Composable
fun ProductInputText(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    maxLine: Int = 1,
    onTextChange: (String) -> Unit,
    onImeAction: () -> Unit = {}){

    val keyBoardController = LocalSoftwareKeyboardController.current
    TextField(
        value = text,
        onValueChange = onTextChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent),
        maxLines = maxLine,
        label = { Text(text = label)},
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            onImeAction()
            keyBoardController?.hide()
        }),
        modifier = modifier
    )
}

