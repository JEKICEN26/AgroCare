package com.zaky.agrocare.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.data.StockManager
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.data.local.CartEntity
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartDao = AppDatabase.getDatabase(application, viewModelScope).cartDao()

    // Ambil data langsung dari Room (Flow -> LiveData)
    val cartItems: LiveData<List<CartItem>> = cartDao.getAllCartItems().asLiveData().map { entities ->
        entities.map { entity ->
            CartItem(entity.id, entity.name, entity.price, entity.quantity, entity.imageUrl, entity.stock)
        }
    }

    val totalPrice: LiveData<Int> = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }

    val totalItems: LiveData<Int> = cartItems.map { items ->
        items.sumOf { it.quantity }
    }

    // Event untuk menampilkan pesan stok tidak mencukupi
    private val _stockWarningEvent = MutableLiveData<String?>()
    val stockWarningEvent: LiveData<String?> = _stockWarningEvent

    fun clearStockWarning() {
        _stockWarningEvent.value = null
    }

    fun addToCart(item: CartItem) {
        viewModelScope.launch {
            val stock = StockManager.getStock(item.id)
            val existingEntity = cartDao.getCartItemById(item.id)
            if (existingEntity != null) {
                val newQuantity = existingEntity.quantity + item.quantity
                if (newQuantity > stock) {
                    // Set ke stok maksimum jika melebihi
                    val cappedQuantity = stock.coerceAtLeast(existingEntity.quantity)
                    if (cappedQuantity > existingEntity.quantity) {
                        cartDao.updateCartItem(existingEntity.copy(quantity = cappedQuantity, stock = stock))
                    }
                    _stockWarningEvent.postValue("Stok ${item.name} tidak mencukupi! Tersisa $stock item.")
                } else {
                    cartDao.updateCartItem(existingEntity.copy(quantity = newQuantity, stock = stock))
                }
            } else {
                val actualQuantity = if (item.quantity > stock) stock else item.quantity
                if (item.quantity > stock) {
                    _stockWarningEvent.postValue("Stok ${item.name} tidak mencukupi! Tersisa $stock item.")
                }
                if (actualQuantity > 0) {
                    cartDao.insertCartItem(CartEntity(item.id, item.name, item.price, actualQuantity, item.imageUrl, stock))
                } else {
                    _stockWarningEvent.postValue("Stok ${item.name} habis!")
                }
            }
        }
    }

    fun increaseQuantity(id: Int) {
        viewModelScope.launch {
            val existingEntity = cartDao.getCartItemById(id)
            if (existingEntity != null) {
                val stock = StockManager.getStock(id)
                if (existingEntity.quantity + 1 > stock) {
                    // Sudah mencapai batas stok, tidak bisa ditambah
                    _stockWarningEvent.postValue("Stok ${existingEntity.name} sudah mencapai batas! Tersisa $stock item.")
                } else {
                    cartDao.updateCartItem(existingEntity.copy(quantity = existingEntity.quantity + 1, stock = stock))
                }
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

