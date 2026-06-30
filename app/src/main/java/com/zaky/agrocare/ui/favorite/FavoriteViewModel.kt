package com.zaky.agrocare.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zaky.agrocare.data.Product

class FavoriteViewModel : ViewModel() {

    private val _favoriteProducts = MutableLiveData<Set<Product>>(emptySet())
    val favoriteProducts: LiveData<Set<Product>> = _favoriteProducts

    fun toggleFavorite(product: Product) {
        val currentSet = _favoriteProducts.value?.toMutableSet() ?: mutableSetOf()
        
        // Cek apakah product dengan ID ini sudah ada di favorit
        val existingProduct = currentSet.find { it.id == product.id }
        
        if (existingProduct != null) {
            currentSet.remove(existingProduct)
        } else {
            currentSet.add(product)
        }
        
        _favoriteProducts.value = currentSet
    }

    fun isFavorite(productId: Int): Boolean {
        val currentSet = _favoriteProducts.value ?: return false
        return currentSet.any { it.id == productId }
    }
}
