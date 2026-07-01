package com.zaky.agrocare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String
)
