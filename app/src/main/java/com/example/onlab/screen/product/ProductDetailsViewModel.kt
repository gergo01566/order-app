package com.example.onlab.screen.product

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.model.MProduct
import com.example.onlab.navigation.DestinationOneArg
import com.example.onlab.navigation.DestinationProductDetails
import com.example.onlab.navigation.DestinationProductList
import com.example.onlab.service.ProductStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val storageService: ProductStorageService,
    savedStateHandle: SavedStateHandle
) : OrderAppViewModel() {

    val product = mutableStateOf(MProduct())

    init {
        val productId = savedStateHandle.get<String>(DestinationOneArg)
        launchCatching {
            //product.value = storageService.getProduct(productId!!) ?: MProduct()
        }
    }

    fun onDeleteProduct(productId: String, navigateFromTo: (String, String) -> Unit){
        launchCatching {
            storageService.deleteProduct(productId){
                navigateFromTo(DestinationProductDetails, DestinationProductList)
            }
        }
    }

    fun onTitleChange(newValue: String) {
        product.value = product.value.copy(title = newValue)
    }

    fun onCategoryChange(newValue: String) {
        product.value = product.value.copy(category = newValue)
    }

    fun onPricePieceChange(newValue: String) {
        product.value = product.value.copy(pricePerPiece = newValue.toInt())
    }

    fun onPriceCartonChange(newValue: String) {
        product.value = product.value.copy(pricePerKarton = newValue.toInt())
    }

    fun onImageChange(newValue: String) {
        product.value = product.value.copy(image = newValue)
    }

    private fun onUpdateProduct(product: MProduct, navigateFromTo: (String, String) -> Unit) {
        launchCatching {
            storageService.updateProduct(product){
                navigateFromTo(DestinationProductDetails, DestinationProductList)
            }
        }
    }

    private fun onSaveProduct(product: MProduct, navigateFromTo: (String, String) -> Unit){
        launchCatching {
            storageService.saveProduct(product){
                navigateFromTo(DestinationProductDetails, DestinationProductList)
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