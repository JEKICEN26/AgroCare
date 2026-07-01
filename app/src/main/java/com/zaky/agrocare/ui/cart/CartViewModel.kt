package com.zaky.agrocare.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.data.local.CartEntity
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartDao = AppDatabase.getDatabase(application, viewModelScope).cartDao()

    // Ambil data langsung dari Room (Flow -> LiveData)
    val cartItems: LiveData<List<CartItem>> = cartDao.getAllCartItems().asLiveData().map { entities ->
        entities.map { entity ->
            CartItem(entity.id, entity.name, entity.price, entity.quantity, entity.imageUrl)
        }
    }

    val totalPrice: LiveData<Int> = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }

    val totalItems: LiveData<Int> = cartItems.map { items ->
        items.sumOf { it.quantity }
    }

    fun addToCart(item: CartItem) {
        viewModelScope.launch {
            val existingEntity = cartDao.getCartItemById(item.id)
            if (existingEntity != null) {
                cartDao.updateCartItem(existingEntity.copy(quantity = existingEntity.quantity + item.quantity))
            } else {
                cartDao.insertCartItem(CartEntity(item.id, item.name, item.price, item.quantity, item.imageUrl))
            }
        }
    }

    fun increaseQuantity(id: Int) {
        viewModelScope.launch {
            val existingEntity = cartDao.getCartItemById(id)
            if (existingEntity != null) {
                cartDao.updateCartItem(existingEntity.copy(quantity = existingEntity.quantity + 1))
            }
        }
    }

    fun decreaseQuantity(id: Int) {
        viewModelScope.launch {
            val existingEntity = cartDao.getCartItemById(id)
            if (existingEntity != null) {
                if (existingEntity.quantity > 1) {
                    cartDao.updateCartItem(existingEntity.copy(quantity = existingEntity.quantity - 1))
                } else {
                    cartDao.deleteCartItem(id)
                }
            }
        }
    }

    fun removeCartItem(id: Int) {
        viewModelScope.launch {
            cartDao.deleteCartItem(id)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCart()
        }
    }

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
