package com.zaky.agrocare.data

import android.content.Context
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.data.local.ProductDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Singleton StockManager sebagai sumber kebenaran tunggal untuk stok produk.
 * Mengelola stok untuk produk hardcoded (di memory) dan produk database (via ProductDao).
 *
 * Produk hardcoded (ID 1-7): stok disimpan di memory map.
 * Produk database (ID dari tabel products): stok dibaca/ditulis via ProductDao.
 */
object StockManager {

    private lateinit var productDao: ProductDao
    private val scope = CoroutineScope(Dispatchers.IO)
    private var isInitialized = false

    // Stok produk hardcoded disimpan di memory
    private val hardcodedStock = mutableMapOf(
        1 to 50,  // Tomat Merah Segar Organik
        2 to 50,  // Bibit Cabai Rawit Unggul
        3 to 50,  // Pupuk Kompos Premium 5kg
        4 to 50,  // Bibit Padi Inpari 32
        5 to 50,  // Pupuk Kandang Premium
        6 to 50,  // Benih Jagung Hibrida
        7 to 50   // Pupuk Organik Cair (POC)
    )

    fun init(context: Context) {
        if (isInitialized) return
        productDao = AppDatabase.getDatabase(context, scope).productDao()
        isInitialized = true
    }

    /**
     * Mengambil stok produk.
     * - Jika produk hardcoded (ID 1-7), ambil dari memory map.
     * - Jika produk database, ambil dari tabel products.
     */
    fun getStock(productId: Int): Int {
        // Cek di hardcoded stock terlebih dahulu
        hardcodedStock[productId]?.let { return it }

        // Jika bukan hardcoded, cek di database
        if (!isInitialized) return Int.MAX_VALUE
        return try {
            runBlocking(Dispatchers.IO) {
                productDao.getProductById(productId)?.stock ?: Int.MAX_VALUE
            }
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }

    /**
     * Mengurangi stok produk.
     * - Jika produk hardcoded, kurangi di memory map.
     * - Jika produk database, update via ProductDao.
     */
    fun reduceStock(productId: Int, quantity: Int) {
        if (hardcodedStock.containsKey(productId)) {
            // Produk hardcoded: kurangi di memory
            val currentStock = hardcodedStock[productId] ?: return
            val newStock = (currentStock - quantity).coerceAtLeast(0)
            hardcodedStock[productId] = newStock
        } else {
            // Produk database: kurangi via DAO
            if (!isInitialized) return
            scope.launch {
                val product = productDao.getProductById(productId)
                if (product != null) {
                    val newStock = (product.stock - quantity).coerceAtLeast(0)
                    productDao.updateStock(productId, newStock)
                }
            }
        }
    }

    /**
     * Mengecek apakah produk adalah hardcoded
     */
    fun isHardcodedProduct(productId: Int): Boolean {
        return hardcodedStock.containsKey(productId)
    }

    /**
     * Reset stok hardcoded ke nilai awal (digunakan saat reset data)
     */
    fun resetHardcodedStock() {
        hardcodedStock[1] = 50
        hardcodedStock[2] = 50
        hardcodedStock[3] = 50
        hardcodedStock[4] = 50
        hardcodedStock[5] = 50
        hardcodedStock[6] = 50
        hardcodedStock[7] = 50
    }
}
