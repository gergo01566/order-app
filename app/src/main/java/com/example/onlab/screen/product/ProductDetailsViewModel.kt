package com.example.onlab.screen.product

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MProduct
import com.example.onlab.navigation.DestinationOneArg
import com.example.onlab.navigation.DestinationProductDetails
import com.example.onlab.navigation.DestinationProductList
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val storageService: ProductStorageService,
    savedStateHandle: SavedStateHandle
) : OrderAppViewModel() {

    var productResponse by mutableStateOf<ValueOrException<MProduct>>(ValueOrException.Loading)
        private set

    var deleteProductResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var updateProductResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var saveProductResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var state = mutableStateOf(ProductUiState())
        private set

    init {
        val productId = savedStateHandle.get<String>(DestinationOneArg)
        launchCatching {
            if(!productId.isNullOrEmpty()){
                productResponse = storageService.getProduct(productId)
                when (productResponse) {
                    is ValueOrException.Success<MProduct> ->  {
                        val data = (productResponse as ValueOrException.Success<MProduct>).data
                        if (!data.id.isNullOrEmpty()){
                            state.value = state.value.copy(
                                id = data.id,
                                title = data.title,
                                pricePerPiece = data.pricePerPiece.toString(),
                                pricePerCarton = data.pricePerKarton.toString(),
                                category = data.category,
                                image = data.image
                            )
                        }
                    }
                    else -> {}
            }
            } else {
                productResponse = ValueOrException.Success(MProduct())
            }
        }
    }

    fun onTitleChange(newValue: String) {
        state.value = state.value.copy(title = newValue)
    }

    fun onCategoryChange(newValue: String) {
        state.value = state.value.copy(category = newValue)
    }

    fun onPricePieceChange(newValue: String) {
        state.value = state.value.copy(pricePerPiece = newValue)
    }

    fun onPriceCartonChange(newValue: String) {
        state.value = state.value.copy(pricePerCarton = newValue)
    }

    fun onImageChange(newValue: String) {
        state.value = state.value.copy(image = newValue)
    }
    fun onDeleteProduct(productId: String, navigateFromTo: (String, String) -> Unit){
        launchCatching {
            deleteProductResponse = ValueOrException.Loading
            deleteProductResponse = storageService.deleteProduct(productId)
            when(deleteProductResponse) {
                is ValueOrException.Success<Boolean> -> {
                    if ((deleteProductResponse as ValueOrException.Success<Boolean>).data) {
                        navigateFromTo(DestinationProductDetails, DestinationProductList)
                    } else {
                        Log.d("TAG", "onDeleteProduct: hiba")
                    }
                }
                is ValueOrException.Failure -> {
                    val exception = (deleteProductResponse as ValueOrException.Failure).e
                    Log.d("TAG", "onDeleteProduct: $exception")
                }
                else -> {
                    Log.d("TAG", "onDeleteProduct: $deleteProductResponse")
                }
            }
        }
    }

    private fun onUpdateProduct(product: MProduct, navigateFromTo: (String, String) -> Unit) {
        launchCatching {
            updateProductResponse = ValueOrException.Loading
            updateProductResponse = storageService.updateProduct(product)
            when(updateProductResponse) {
                is ValueOrException.Success<Boolean> -> {
                    if ((updateProductResponse as ValueOrException.Success<Boolean>).data) {
                        navigateFromTo(DestinationProductDetails, DestinationProductList)
                    } else {
                        Log.d("TAG", "onUpdate: hiba")
                    }
                }
                is ValueOrException.Failure -> {
                    val exception = (updateProductResponse as ValueOrException.Failure).e
                    Log.d("TAG", "onUpdate: $exception")
                }
                else -> {
                    Log.d("TAG", "onUpdate: $updateProductResponse")
                }
            }
        }
    }

    private fun onSaveProduct(product: MProduct, navigateFromTo: (String, String) -> Unit){
        launchCatching {
            saveProductResponse = ValueOrException.Loading
            delay(500)
            saveProductResponse = storageService.saveProduct(product)
            when(saveProductResponse) {
                is ValueOrException.Success<Boolean> -> {
                    if ((saveProductResponse as ValueOrException.Success<Boolean>).data) {
                        navigateFromTo(DestinationProductDetails, DestinationProductList)
                    } else {
                        Log.d("TAG", "onSave: hiba")
                    }
                }
                is ValueOrException.Failure -> {
                    val exception = (saveProductResponse as ValueOrException.Failure).e
                    Log.d("TAG", "onSave: $exception")
                }
                else -> {
                    Log.d("TAG", "onSave: $saveProductResponse")
                }
            }
        }
    }

    fun onDoneClick(product: MProduct, navigateFromTo: (String, String) -> Unit){
        if (product.id!!.isBlank()){
            onSaveProduct(product, navigateFromTo)
        } else {
            onUpdateProduct(product, navigateFromTo)
        }
    }
}