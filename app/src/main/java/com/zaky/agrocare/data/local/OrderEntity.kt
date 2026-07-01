package com.zaky.agrocare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val productId: Int,
    val storeName: String,
    val productName: String,
    val imageUrl: String,
    val price: Int,
    val quantity: Int,
    var status: String,
    var statusId: Int, // 1: Belum Bayar, 2: Dikemas, 3: Dikirim, 4: Selesai, 5: Dibatalkan
    var isRated: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(), // Untuk sorting
    val address: String = "",
    var paymentMethod: String = ""
)
