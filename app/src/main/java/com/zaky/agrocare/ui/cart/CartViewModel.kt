package com.zaky.agrocare.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.zaky.agrocare.data.CartItem

class CartViewModel : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems

    val totalPrice: LiveData<Int> = _cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }

    val totalItems: LiveData<Int> = _cartItems.map { items ->
        items.sumOf { it.quantity }
    }

    fun addToCart(item: CartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == item.id }

        if (index != -1) {
            val existingItem = currentList[index]
            currentList[index] = existingItem.copy(quantity = existingItem.quantity + item.quantity)
        } else {
            currentList.add(item)
        }
        _cartItems.value = ArrayList(currentList)
    }

    fun increaseQuantity(id: Int) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = currentList[index]
            currentList[index] = item.copy(quantity = item.quantity + 1)
            _cartItems.value = ArrayList(currentList)
        }
    }

    fun decreaseQuantity(id: Int) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = currentList[index]
            if (item.quantity > 1) {
                currentList[index] = item.copy(quantity = item.quantity - 1)
            } else {
                currentList.removeAt(index)
            }
            _cartItems.value = ArrayList(currentList)
        }
    }

    fun removeCartItem(id: Int) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        currentList.removeAll { it.id == id }
        _cartItems.value = ArrayList(currentList)
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
