package com.zaky.agrocare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val email: String,
    val phone: String,
    val passwordHash: String, // Sebaiknya disamarkan/hash, namun untuk contoh kita simpan plain atau md5
    val profileImage: String? = null
)
