package com.zaky.agrocare.data.local

data class OrderEntity(
    val id: String = "",
    val productId: Int = 0,
    val storeName: String = "",
    val productName: String = "",
    val imageUrl: String = "",
    val price: Int = 0,
    val quantity: Int = 0,
    var status: String = "",
    var statusId: Int = 0, // 1: Belum Bayar, 2: Dikemas, 3: Dikirim, 4: Selesai, 5: Dibatalkan
    var isRated: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(), // Untuk sorting
    val address: String = "",
    var paymentMethod: String = ""
)
