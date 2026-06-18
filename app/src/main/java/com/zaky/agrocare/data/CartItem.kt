package com.zaky.agrocare.data

data class CartItem(
    val id: Int,
    val name: String,
    val price: Int,
    val quantity: Int, // Diubah menjadi val agar DiffUtil bekerja maksimal
    val imageUrl: String
)
