package com.zaky.agrocare.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zaky.agrocare.data.local.UserEntity
import com.zaky.agrocare.data.local.ProductEntity
import com.zaky.agrocare.data.local.OrderEntity
import com.zaky.agrocare.data.local.AddressEntity
import kotlinx.coroutines.tasks.await

/**
 * Singleton repository untuk berinteraksi dengan Firebase Firestore
 */
object FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // --- USERS ---
    
    suspend fun saveUser(user: UserEntity): Boolean {
        return try {
            db.collection("users").document(user.username).set(user).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error saving user", e)
            false
        }
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return try {
            val snapshot = db.collection("users").document(username).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(UserEntity::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting user", e)
            null
        }
    }

    suspend fun checkUsernameExists(username: String): Boolean {
        return getUserByUsername(username) != null
    }

    // --- PRODUCTS ---

    suspend fun getAllProducts(): List<ProductEntity> {
        return try {
            val snapshot = db.collection("products").get().await()
            snapshot.toObjects(ProductEntity::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting products", e)
            emptyList()
        }
    }
    
    suspend fun initDummyProductsIfEmpty() {
        val products = getAllProducts()
        if (products.isEmpty()) {
            val dummyProducts = listOf(
                ProductEntity(id = 1, name = "Bawang Merah", price = 40000, unit = "kg", stock = 50, category = "Sayur", imageName = "img_bawang_merah"),
                ProductEntity(id = 2, name = "Cabe Rawit Merah", price = 50000, unit = "kg", stock = 25, category = "Sayur", imageName = "img_cabe_rawit"),
                ProductEntity(id = 3, name = "Tomat", price = 30000, unit = "kg", stock = 40, category = "Sayur", imageName = "img_tomat"),
                ProductEntity(id = 4, name = "Wortel", price = 8000, unit = "kg", stock = 100, category = "Sayur", imageName = "img_wortel")
            )
            for (product in dummyProducts) {
                db.collection("products").document(product.id.toString()).set(product).await()
            }
        }
    }
    
    suspend fun updateProductStock(productId: Int, quantityToReduce: Int) {
        try {
            val docRef = db.collection("products").document(productId.toString())
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentStock = snapshot.getLong("stock") ?: 0
                transaction.update(docRef, "stock", currentStock - quantityToReduce)
            }.await()
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error updating stock", e)
        }
    }

    // --- ORDERS ---

    suspend fun saveOrders(orders: List<OrderEntity>): Boolean {
        return try {
            val batch = db.batch()
            for (order in orders) {
                val docRef = db.collection("orders").document(order.id)
                batch.set(docRef, order)
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error saving orders", e)
            false
        }
    }

    suspend fun getAllOrders(): List<OrderEntity> {
        return try {
            val snapshot = db.collection("orders").orderBy("timestamp", Query.Direction.DESCENDING).get().await()
            snapshot.toObjects(OrderEntity::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting orders", e)
            emptyList()
        }
    }
    
    suspend fun getOrderById(orderId: String): OrderEntity? {
        return try {
            val snapshot = db.collection("orders").document(orderId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(OrderEntity::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting order by id", e)
            null
        }
    }
    
    suspend fun updateOrder(order: OrderEntity): Boolean {
        return try {
            db.collection("orders").document(order.id).set(order).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error updating order", e)
            false
        }
    }
    
    suspend fun clearAllOrders() {
        try {
            val snapshot = db.collection("orders").get().await()
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        } catch (e: Exception) {
             Log.e("FirebaseRepo", "Error clearing orders", e)
        }
    }

    // --- ADDRESSES ---

    suspend fun saveAddress(address: AddressEntity): Boolean {
        return try {
            db.collection("addresses").document(address.id.toString()).set(address).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error saving address", e)
            false
        }
    }

    suspend fun getAllAddresses(): List<AddressEntity> {
        return try {
            val snapshot = db.collection("addresses").get().await()
            snapshot.toObjects(AddressEntity::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error getting addresses", e)
            emptyList()
        }
    }
}
