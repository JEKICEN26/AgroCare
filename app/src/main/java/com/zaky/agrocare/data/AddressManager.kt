package com.zaky.agrocare.data

import android.content.Context
import com.zaky.agrocare.data.local.AddressDao
import com.zaky.agrocare.data.local.AddressEntity
import com.zaky.agrocare.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object AddressManager {
    private lateinit var addressDao: AddressDao
    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        if (isInitialized) return
        val database = AppDatabase.getDatabase(context, scope)
        addressDao = database.addressDao()
        isInitialized = true
    }

    fun getAllAddresses(): Flow<List<AddressEntity>> {
        checkInitialized()
        return addressDao.getAllAddresses()
    }

    suspend fun getDefaultAddress(): AddressEntity? {
        checkInitialized()
        return addressDao.getDefaultAddress() ?: addressDao.getLatestAddress()
    }

    suspend fun addAddress(title: String, fullAddress: String, lat: Double? = null, lng: Double? = null, isDefault: Boolean = false) {
        checkInitialized()
        if (isDefault) {
            addressDao.clearDefaultAddress()
        }
        val entity = AddressEntity(
            title = title,
            fullAddress = fullAddress,
            latitude = lat,
            longitude = lng,
            isDefault = isDefault
        )
        addressDao.insertAddress(entity)
    }

    suspend fun updateAddress(entity: AddressEntity) {
        checkInitialized()
        if (entity.isDefault) {
            addressDao.clearDefaultAddress()
        }
        addressDao.updateAddress(entity)
    }
    
    suspend fun deleteAddress(entity: AddressEntity) {
        checkInitialized()
        addressDao.deleteAddress(entity)
    }
    
    suspend fun setAsDefault(entity: AddressEntity) {
        checkInitialized()
        addressDao.clearDefaultAddress()
        entity.isDefault = true
        addressDao.updateAddress(entity)
    }

    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("AddressManager must be initialized first")
        }
    }
}
