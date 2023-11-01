package com.example.onlab.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.onlab.components.*
import com.example.onlab.model.*
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.screen.product.ProductListViewModel
import com.example.onlab.viewModels.*
import java.util.*
import com.example.onlab.model.Category as Categ
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onlab.data.DataOrException
import com.example.onlab.data.ValueOrException
import com.example.onlab.navigation.DestinationProductDetails
import com.example.onlab.navigation.DestinationProductList


@Composable
fun ProductListScreen(
    onNavigate: (String) -> Unit,
    navigateFromTo: (String, String) -> Unit,
    orderID: String? = null,
    ordering: Boolean,
    navigateBack: () -> Unit,
    orderItemViewModel: MOrderItemViewModel,
    newViewModel: ProductListViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    val showFullScreenDialog = remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<MProduct?>(null) }
    val context = LocalContext.current
    //val loadingState by newViewModel.loadingState

    //val viewModel = remember { ProductListViewModel() }


    //var listOfProducts = newViewModel.searchResults.collectAsStateWithLifecycle(emptyList())
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Categ?>(com.example.onlab.model.Category.Összes) }

    //val data = newViewModel.data.collectAsState(DataOrException(emptyList(), false, null))
    //val data = newViewModel.data.value.data?.collectAsStateWithLifecycle(initialValue = emptyList())
    //val loadingState by newViewModel.loading.collectAsStateWithLifecycle(LoadingState.LOADING)

    showConfirmationDialog(
        showDialog = showDialog,
        message = "Biztos törölni szeretnéd a következő terméket?",
        onConfirm = {
            selectedProduct?.let {
                newViewModel.onDeleteProduct(it.id.toString()){
                    Toast.makeText(context, "Termék törölve!", Toast.LENGTH_SHORT).show()
                    showDialog.value = false}
            }
        }
    ) {
        showDialog.value = false
    }

    selectedProduct?.let {
        FullScreenDialog(
            showDialog = showFullScreenDialog,
            selectedProduct = it,
            isKarton = null,
            currentQuantity = null,
            onAdd = { state: Boolean, quantity: String ->
                if (quantity.isDigitsOnly() && quantity!=""){
                    val orderItem = MOrderItem(
                        id = UUID.randomUUID().toString(),
                        amount = quantity.toInt(),
                        orderID = orderID!!,
                        productID = selectedProduct!!.id.toString(),
                        statusID = 0,
                        carton = !state,
                        piece = state
                    )
                    orderItemViewModel.addOrderItem(orderItem)
                    Log.d("DB", "ADD, lista tartalma ennyi elem: ${orderItemViewModel.getOrderItemsList().size}")
                    navigateBack()
//                    orderItemViewModel.saveOrderItemToFirebase(orderItem, {
//                        navController.popBackStack()
//                    })
                } else {
                    Toast.makeText(context, "A mennyiség megadásánál csak számokat használj!", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            showFullScreenDialog.value = false
        }
    }

    Scaffold(
        topBar = {
            if (ordering) createTopBar(text = "Új rendelés", withIcon = true, onBack = {
                navigateBack()
            })
            else {
                createTopBar(text = "Termékek", withIcon = false){
                    navigateBack()
                }
            }
        },
        bottomBar = {
            if (!ordering) BottomNavBar(selectedItem = items[2]){
                navigateFromTo(DestinationProductList, it)
            }
        },
        floatingActionButton = {
            if(!ordering){
                ExtendedFloatingActionButton(
                    modifier =  Modifier.padding(bottom = 60.dp),
                    text = { Text(text = "Új termék") },
                    onClick = {
                        navigateFromTo(DestinationProductList, DestinationProductDetails)
                    },
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = MaterialTheme.colors.primary,
                )
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        content = { it ->
            when(val productsResponse = newViewModel.productsResponse){
                is ValueOrException.Loading -> CircularProgressIndicator()
                is ValueOrException.Failure -> CircularProgressIndicator()
                is ValueOrException.Success -> {
                    it.calculateBottomPadding()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = it.calculateBottomPadding())
                    ) {
                        MenuBar(
                            categories = getCategoryTypes(com.example.onlab.model.Category::class.java),
                            selectedCategory = selectedCategory,
                            onCategorySelected = { category ->
                                newViewModel.onCategoryChanged(category = category!!)
                                selectedCategory = category
                            }
                        )

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = searchText,
                                onValueChange = { newText ->
                                    newViewModel.onSearchTextChanged(newText)
                                    searchText = newText },
                                placeholder = { Text(text = "Keresés") })
                        }

                        CreateList(

                            data = productsResponse.data.sortedBy { it.title },
                            //data = listOfProducts!!.value.sortedBy { it.title },
                            //data = data.value.data!!.sortedBy { it.title },
                            onDelete = {
                                Log.d("TAG", "ProductListScreen: ${it.id}")
                                showDialog.value = true
                                selectedProduct = it },
                            onEdit = {
                                onNavigate(it.id.toString())
                                //navController.navigate(route = ProductScreens.DetailsScreen.name+"/${it.id}")
                                //Log.d("ID", "ProductListScreen: ${it.id}")
                            },
                            onClick = {
                                if(ordering) {
                                    selectedProduct = it
                                    showFullScreenDialog.value = true
                                }
                                else{
                                    Log.d("TAG", "ProductListScreen: ez nem jott ossze")
                                }
                            },
                            iconClickEnabled = !ordering,
                            itemContent = { product ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.size(70.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        AsyncImage(
                                            model = product.image.toUri(),
                                            contentDescription = "profile image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(80.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(10.dp)
                                    ) {
                                        Text(text = product.title, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${product.pricePerPiece}HUF / ${product.pricePerKarton}HUF",
                                            style = MaterialTheme.typography.caption
                                        )

                                    }
                                }
                            })

                    }
                }
            }
//            if (newViewModel.loadingState.value == LoadingState.LOADING){
//                CircularProgressIndicator()
//            } else {

//            if (newViewModel.state.collectAsState().value == LoadingState.LOADING){
//                //Log.d("LOAD", "ProductListScreen: ${data.value.loading}")
//                CircularProgressIndicator()
//            } else if (newViewModel.state.collectAsState().value == LoadingState.LOADED) {

           // }
        }
    )
}


@Composable
fun MenuBar(
    categories: List<Categ>,
    selectedCategory: Categ?,
    onCategorySelected: (Categ?) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(13.dp)
    ) {

        items(categories) { category ->
            Text(
                text = category.name,
                color = if (category == selectedCategory) MaterialTheme.colors.primary else Color.Gray,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = { onCategorySelected(category) })
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    text: Int,
    value: String,
    onValueChange: (String) -> Unit,
){
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { newText -> onValueChange(newText) },
        placeholder = { Text(stringResource(id = text)) })
}





