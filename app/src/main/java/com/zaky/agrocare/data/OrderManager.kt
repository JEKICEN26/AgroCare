package com.zaky.agrocare.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zaky.agrocare.data.local.OrderEntity
import com.zaky.agrocare.data.remote.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Singleton OrderManager sebagai sumber kebenaran tunggal untuk semua pesanan.
 * Mengelola state pesanan di seluruh aplikasi dengan dukungan Firebase Firestore.
 */
object OrderManager {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isInitialized = false

    private var _ordersLiveData: MutableLiveData<List<Order>> = MutableLiveData(emptyList())
    
    // We keep a manual list so synchronous getters work (for simpler refactoring)
    private var currentOrders = mutableListOf<Order>()

    val orders: LiveData<List<Order>> get() = _ordersLiveData

    fun init(context: Context) {
        if (isInitialized) return
        
        loadOrdersFromFirebase()
        
        isInitialized = true
    }
    
    private fun loadOrdersFromFirebase() {
        scope.launch {
            val entities = FirebaseRepository.getAllOrders()
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
            _ordersLiveData.postValue(mapped)
        }
    }

    /**
     * Reset semua pesanan kembali ke state awal simulasi
     */
    fun resetData() {
        if (!isInitialized) return
        scope.launch {
            FirebaseRepository.clearAllOrders()
            val defaultAddress = "Jl. Sudirman No. 45, Jakarta Pusat"
            val initialOrders = listOf(
                OrderEntity(UUID.randomUUID().toString(), 1, "Toko Bibit Unggul", "Bawang Merah", "img_bawang_merah", 40000, 2, "Belum Bayar", 1, timestamp = System.currentTimeMillis() - 10000, address = defaultAddress, paymentMethod = ""),
                OrderEntity(UUID.randomUUID().toString(), 5, "AgroCare Official", "Pupuk Kompos Organik 5Kg", "ic_organic_fertilizer", 75000, 1, "Belum Bayar", 1, timestamp = System.currentTimeMillis() - 20000, address = defaultAddress, paymentMethod = ""),
                OrderEntity(UUID.randomUUID().toString(), 3, "Tani Maju", "Tomat Cherry Hidroponik", "img_tomat", 30000, 3, "Dikirim", 3, timestamp = System.currentTimeMillis() - 30000, address = defaultAddress, paymentMethod = "Virtual Account"),
                OrderEntity(UUID.randomUUID().toString(), 2, "Sayur Segar", "Cabe Rawit Merah", "img_cabe_rawit", 50000, 1, "Selesai", 4, timestamp = System.currentTimeMillis() - 40000, address = defaultAddress, paymentMethod = "Kartu Kredit"),
                OrderEntity(UUID.randomUUID().toString(), 4, "Toko Bibit Unggul", "Wortel", "img_wortel", 8000, 5, "Selesai", 4, timestamp = System.currentTimeMillis() - 50000, address = defaultAddress, paymentMethod = "Virtual Account")
            )
            FirebaseRepository.saveOrders(initialOrders)
            loadOrdersFromFirebase()
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
                    productId = item.id,
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
            FirebaseRepository.saveOrders(entities)
            loadOrdersFromFirebase()
        }
    }

    /**
     * Bayar pesanan: status berubah dari "Belum Bayar" → "Dikemas"
     * Otomatis mengurangi stok produk saat status menjadi "Dikemas"
     */
    fun payOrder(orderId: String, paymentMethod: String) {
        if (!isInitialized) return
        scope.launch {
            val entity = FirebaseRepository.getOrderById(orderId)
            if (entity != null) {
                entity.status = "Dikemas"
                entity.statusId = 2
                entity.paymentMethod = paymentMethod
                FirebaseRepository.updateOrder(entity)
                
                // Kurangi stok produk secara otomatis saat pesanan dikemas
                StockManager.reduceStock(entity.productId, entity.quantity)
                
                loadOrdersFromFirebase()
            }
        }
    }

    /**
     * Membatalkan pesanan: status berubah menjadi "Dibatalkan"
     */
    fun cancelOrder(orderId: String) {
        if (!isInitialized) return
        scope.launch {
            val entity = FirebaseRepository.getOrderById(orderId)
            if (entity != null) {
                entity.status = "Dibatalkan"
                entity.statusId = 5
                FirebaseRepository.updateOrder(entity)
                loadOrdersFromFirebase()
            }
        }
    }

    /**
     * Set rating pada pesanan: tombol berubah menjadi "Beli Lagi"
     */
    fun rateOrder(orderId: String) {
        if (!isInitialized) return
        scope.launch {
            val entity = FirebaseRepository.getOrderById(orderId)
            if (entity != null) {
                entity.isRated = true
                FirebaseRepository.updateOrder(entity)
                loadOrdersFromFirebase()
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
