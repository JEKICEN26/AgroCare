package com.zaky.agrocare.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.data.local.OrderDao
import com.zaky.agrocare.data.local.OrderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Singleton OrderManager sebagai sumber kebenaran tunggal untuk semua pesanan.
 * Mengelola state pesanan di seluruh aplikasi dengan dukungan Room Database.
 */
object OrderManager {

    private lateinit var orderDao: OrderDao
    private val scope = CoroutineScope(Dispatchers.IO)
    private var isInitialized = false

    // _orders now act as an in-memory mirror if needed, but we expose the Room LiveData
    private var _ordersLiveData: LiveData<List<Order>> = MutableLiveData(emptyList())
    
    // We keep a manual list so synchronous getters work (for simpler refactoring)
    private var currentOrders = mutableListOf<Order>()

    val orders: LiveData<List<Order>> get() = _ordersLiveData

    fun init(context: Context) {
        if (isInitialized) return
        orderDao = AppDatabase.getDatabase(context, scope).orderDao()
        
        // Map dari Entity ke Data Class UI
        _ordersLiveData = orderDao.getAllOrders().map { entities ->
            val mapped = entities.map { entity ->
                Order(
                    entity.id,
                    entity.productId,
                    entity.storeName,
                    entity.productName,
                    entity.imageUrl,
                    entity.price,
                    entity.quantity,
                    entity.status,
                    entity.statusId,
                    address = entity.address,
                    paymentMethod = entity.paymentMethod
                ).apply {
                    isRated = entity.isRated
                }
            }
            currentOrders = mapped.toMutableList()
            mapped
        }.asLiveData()
        
        isInitialized = true
    }

    /**
     * Reset semua pesanan kembali ke state awal simulasi
     */
    fun resetData() {
        if (!isInitialized) return
        scope.launch {
            orderDao.clearAllOrders()
            // Data awal akan di-seed otomatis oleh AppDatabase saat kosong jika kita mau, 
            // tapi untuk force reset kita insert ulang
            val defaultAddress = "Jl. Sudirman No. 45, Jakarta Pusat"
            val initialOrders = listOf(
                OrderEntity(UUID.randomUUID().toString(), 1, "Toko Bibit Unggul", "Bawang Merah", "img_bawang_merah", 40000, 2, "Belum Bayar", 1, timestamp = System.currentTimeMillis() - 10000, address = defaultAddress, paymentMethod = ""),
                OrderEntity(UUID.randomUUID().toString(), 5, "AgroCare Official", "Pupuk Kompos Organik 5Kg", "ic_organic_fertilizer", 75000, 1, "Belum Bayar", 1, timestamp = System.currentTimeMillis() - 20000, address = defaultAddress, paymentMethod = ""),
                OrderEntity(UUID.randomUUID().toString(), 3, "Tani Maju", "Tomat Cherry Hidroponik", "img_tomat", 30000, 3, "Dikirim", 3, timestamp = System.currentTimeMillis() - 30000, address = defaultAddress, paymentMethod = "Virtual Account"),
                OrderEntity(UUID.randomUUID().toString(), 2, "Sayur Segar", "Cabe Rawit Merah", "img_cabe_rawit", 50000, 1, "Selesai", 4, timestamp = System.currentTimeMillis() - 40000, address = defaultAddress, paymentMethod = "Kartu Kredit"),
                OrderEntity(UUID.randomUUID().toString(), 4, "Toko Bibit Unggul", "Wortel", "img_wortel", 8000, 5, "Selesai", 4, timestamp = System.currentTimeMillis() - 50000, address = defaultAddress, paymentMethod = "Virtual Account")
            )
            orderDao.insertOrders(initialOrders)
        }
    }

    /**
     * Menambahkan pesanan baru dari item keranjang
     */
    fun addOrderFromCart(items: List<CartItem>, address: String) {
        if (!isInitialized) return
        scope.launch {
            val entities = items.map { item ->
                OrderEntity(
                    id = UUID.randomUUID().toString(),
                    productId = item.id, // ID asli dari produk!
                    storeName = "AgroCare Official",
                    productName = item.name,
                    imageUrl = item.imageUrl,
                    price = item.price,
                    quantity = item.quantity,
                    status = "Belum Bayar",
                    statusId = 1,
                    timestamp = System.currentTimeMillis(),
                    address = address,
                    paymentMethod = ""
                )
            }
            orderDao.insertOrders(entities)
        }
    }

    /**
     * Bayar pesanan: status berubah dari "Belum Bayar" → "Dikemas"
     * Otomatis mengurangi stok produk saat status menjadi "Dikemas"
     */
    fun payOrder(orderId: String, paymentMethod: String) {
        if (!isInitialized) return
        scope.launch {
            val entity = orderDao.getOrderById(orderId)
            if (entity != null) {
                entity.status = "Dikemas"
                entity.statusId = 2
                entity.paymentMethod = paymentMethod
                orderDao.updateOrder(entity)
                
                // Kurangi stok produk secara otomatis saat pesanan dikemas
                StockManager.reduceStock(entity.productId, entity.quantity)
            }
        }
    }

    /**
     * Membatalkan pesanan: status berubah menjadi "Dibatalkan"
     */
    fun cancelOrder(orderId: String) {
        if (!isInitialized) return
        scope.launch {
            val entity = orderDao.getOrderById(orderId)
            if (entity != null) {
                entity.status = "Dibatalkan"
                entity.statusId = 5
                orderDao.updateOrder(entity)
            }
        }
    }

    /**
     * Set rating pada pesanan: tombol berubah menjadi "Beli Lagi"
     */
    fun rateOrder(orderId: String) {
        if (!isInitialized) return
        scope.launch {
            val entity = orderDao.getOrderById(orderId)
            if (entity != null) {
                entity.isRated = true
                orderDao.updateOrder(entity)
            }
        }
    }

    /**
     * Menghitung jumlah pesanan per status untuk badge di Profile
     */
    fun getCountByStatus(statusId: Int): Int {
        return currentOrders.count { it.statusId == statusId }
    }

    /**
     * Menghitung jumlah pesanan selesai yang belum di-rating
     */
    fun getUnratedFinishedCount(): Int {
        return currentOrders.count { it.statusId == 4 && !it.isRated }
    }

    /**
     * Mendapatkan daftar pesanan berdasarkan filter status
     */
    fun getFilteredOrders(statusIndex: Int): List<Order> {
        return if (statusIndex == 0) {
            currentOrders.toList()
        } else {
            currentOrders.filter { it.statusId == statusIndex }
        }
    }
}
