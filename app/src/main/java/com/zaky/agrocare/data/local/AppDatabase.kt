package com.zaky.agrocare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ProductEntity::class, CartEntity::class, OrderEntity::class, AddressEntity::class, UserEntity::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agrocare_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            scope.launch(Dispatchers.IO) {
                                val database = getDatabase(context, scope)
                                populateDatabase(database.productDao(), database.orderDao(), database.cartDao(), database.addressDao())
                            }
                        }
                        
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            scope.launch(Dispatchers.IO) {
                                val database = getDatabase(context, scope)
                                // Jika produk kosong setelah migrasi, paksa populate lagi
                                if (database.productDao().getAllProductsSync().isEmpty()) {
                                    populateDatabase(database.productDao(), database.orderDao(), database.cartDao(), database.addressDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(productDao: ProductDao, orderDao: OrderDao, cartDao: CartDao, addressDao: AddressDao) {
            val dummyProducts = listOf(
                ProductEntity(name = "Bawang Merah", price = 40000, unit = "kg", stock = 50, category = "Sayur", imageName = "img_bawang_merah"),
                ProductEntity(name = "Cabe Rawit Merah", price = 50000, unit = "kg", stock = 25, category = "Sayur", imageName = "img_cabe_rawit"),
                ProductEntity(name = "Tomat", price = 30000, unit = "kg", stock = 40, category = "Sayur", imageName = "img_tomat"),
                ProductEntity(name = "Wortel", price = 8000, unit = "kg", stock = 100, category = "Sayur", imageName = "img_wortel")
            )
            productDao.insertProducts(dummyProducts)
            
            // Seed Cart (termasuk info stok dari products table)
            cartDao.insertCartItem(CartEntity(1, "Bawang Merah", 40000, 1, "img_bawang_merah", 50))
            cartDao.insertCartItem(CartEntity(2, "Cabe Rawit Merah", 50000, 2, "img_cabe_rawit", 25))
            
            // Seed Address
            val defaultAddress = "Jl. Sudirman No. 45, Jakarta Pusat"
            addressDao.insertAddress(AddressEntity(
                title = "Rumah",
                fullAddress = defaultAddress,
                isDefault = true
            ))
            val initialOrders = listOf(
                OrderEntity(java.util.UUID.randomUUID().toString(), 1, "Toko Bibit Unggul", "Bawang Merah", "img_bawang_merah", 40000, 2, "Belum Bayar", 1, timestamp = System.currentTimeMillis() - 10000, address = defaultAddress, paymentMethod = ""),
                OrderEntity(java.util.UUID.randomUUID().toString(), 5, "AgroCare Official", "Pupuk Kompos Organik 5Kg", "ic_organic_fertilizer", 75000, 1, "Belum Bayar", 1, timestamp = System.currentTimeMillis() - 20000, address = defaultAddress, paymentMethod = ""),
                OrderEntity(java.util.UUID.randomUUID().toString(), 3, "Tani Maju", "Tomat Cherry Hidroponik", "img_tomat", 30000, 3, "Dikirim", 3, timestamp = System.currentTimeMillis() - 30000, address = defaultAddress, paymentMethod = "Virtual Account"),
                OrderEntity(java.util.UUID.randomUUID().toString(), 2, "Sayur Segar", "Cabe Rawit Merah", "img_cabe_rawit", 50000, 1, "Selesai", 4, timestamp = System.currentTimeMillis() - 40000, address = defaultAddress, paymentMethod = "Kartu Kredit"),
                OrderEntity(java.util.UUID.randomUUID().toString(), 4, "Toko Bibit Unggul", "Wortel", "img_wortel", 8000, 5, "Selesai", 4, timestamp = System.currentTimeMillis() - 50000, address = defaultAddress, paymentMethod = "Virtual Account")
            )
            orderDao.insertOrders(initialOrders)
        }
    }
}
