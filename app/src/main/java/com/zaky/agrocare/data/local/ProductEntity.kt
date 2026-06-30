package com.zaky.agrocare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Int,
    val unit: String,
    val stock: Int,
    val category: String,
    val imageName: String
)
