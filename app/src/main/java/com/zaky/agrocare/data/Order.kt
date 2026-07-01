package com.zaky.agrocare.data

data class Order(
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
    val address: String = "",
    var paymentMethod: String = ""
) {
    val totalAmount: Int
        get() = price * quantity
}
