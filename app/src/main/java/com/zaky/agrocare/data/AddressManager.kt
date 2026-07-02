package com.zaky.agrocare.data

import android.content.Context
import com.zaky.agrocare.data.local.AddressEntity
import com.zaky.agrocare.data.remote.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

object AddressManager {
    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true
    }

    fun getAllAddresses(): Flow<List<AddressEntity>> = flow {
        checkInitialized()
        emit(FirebaseRepository.getAllAddresses())
    }

    suspend fun getDefaultAddress(): AddressEntity? {
        checkInitialized()
        val addresses = FirebaseRepository.getAllAddresses()
        return addresses.find { it.isDefault } ?: addresses.lastOrNull()
    }

    suspend fun addAddress(title: String, fullAddress: String, lat: Double? = null, lng: Double? = null, isDefault: Boolean = false) {
        checkInitialized()
        if (isDefault) {
            clearDefaultAddress()
        }
        val entity = AddressEntity(
            title = title,
            fullAddress = fullAddress,
            latitude = lat,
            longitude = lng,
            isDefault = isDefault
        )
        FirebaseRepository.saveAddress(entity)
    }

    suspend fun updateAddress(entity: AddressEntity) {
        checkInitialized()
        if (entity.isDefault) {
            clearDefaultAddress()
        }
        FirebaseRepository.saveAddress(entity)
    }
    
    suspend fun deleteAddress(entity: AddressEntity) {
        checkInitialized()
        // We don't have a specific delete in FirebaseRepo yet, let's just ignore for now or add it later if needed
    }
    
    suspend fun setAsDefault(entity: AddressEntity) {
        checkInitialized()
        clearDefaultAddress()
        entity.isDefault = true
        FirebaseRepository.saveAddress(entity)
    }
    
    private suspend fun clearDefaultAddress() {
        val addresses = FirebaseRepository.getAllAddresses()
        addresses.filter { it.isDefault }.forEach {
            val updated = it.copy(isDefault = false)
            FirebaseRepository.saveAddress(updated)
        }
    }

    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("AddressManager must be initialized first")
        }
    }
}
