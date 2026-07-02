package com.zaky.agrocare.data.local

data class AddressEntity(
    val id: Int = 0,
    val title: String = "", // e.g., "Rumah", "Kantor"
    val fullAddress: String = "", // The detailed typed address
    val latitude: Double? = null,
    val longitude: Double? = null,
    var isDefault: Boolean = false
)
