package com.zaky.agrocare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String, // e.g., "Rumah", "Kantor"
    val fullAddress: String, // The detailed typed address
    val latitude: Double? = null,
    val longitude: Double? = null,
    var isDefault: Boolean = false
)
