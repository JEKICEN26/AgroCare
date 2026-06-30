package com.zaky.agrocare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

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
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            scope.launch(Dispatchers.IO) {
                                val dao = getDatabase(context, scope).productDao()
                                populateDatabase(dao)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(productDao: ProductDao) {
            val dummyProducts = listOf(
                ProductEntity(name = "Bawang Merah", price = 40000, unit = "kg", stock = 50, category = "Sayur", imageName = "img_bawang_merah"),
                ProductEntity(name = "Cabe Rawit Merah", price = 50000, unit = "kg", stock = 25, category = "Sayur", imageName = "img_cabe_rawit"),
                ProductEntity(name = "Tomat", price = 30000, unit = "kg", stock = 40, category = "Sayur", imageName = "img_tomat"),
                ProductEntity(name = "Wortel", price = 8000, unit = "kg", stock = 100, category = "Sayur", imageName = "img_wortel")
            )
            productDao.insertProducts(dummyProducts)
        }
    }
}
