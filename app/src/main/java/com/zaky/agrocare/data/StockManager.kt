package com.zaky.agrocare.data

import android.content.Context
import com.zaky.agrocare.data.remote.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Singleton StockManager sebagai sumber kebenaran tunggal untuk stok produk.
 * Mengelola stok via FirebaseRepository.
 */
object StockManager {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isInitialized = false
    private val memoryStock = mutableMapOf<Int, Int>()

    fun init(context: Context) {
        if (isInitialized) return
        // Inisialisasi awal, load semua produk
        scope.launch {
            val products = FirebaseRepository.getAllProducts()
            products.forEach { 
                memoryStock[it.id] = it.stock 
            }
        }
        isInitialized = true
    }

    /**
     * Mengambil stok produk.
     */
    fun getStock(productId: Int): Int {
        // Cek memory
        memoryStock[productId]?.let { return it }

        // Jika tidak ada di memory, terpaksa ambil secara sinkron (sebaiknya dihindari di produksi)
        return try {
            runBlocking(Dispatchers.IO) {
                val products = FirebaseRepository.getAllProducts()
                val product = products.find { it.id == productId }
                val stock = product?.stock ?: Int.MAX_VALUE
                memoryStock[productId] = stock
                stock
            }
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }

    /**
     * Mengurangi stok produk.
     */
    fun reduceStock(productId: Int, quantity: Int) {
        // Update memory
        val currentStock = memoryStock[productId] ?: return
        val newStock = (currentStock - quantity).coerceAtLeast(0)
        memoryStock[productId] = newStock
        
        // Update Firebase
        scope.launch {
            FirebaseRepository.updateProductStock(productId, quantity)
        }
    }
}
