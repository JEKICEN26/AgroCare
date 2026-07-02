package com.zaky.agrocare.ui.home

import androidx.lifecycle.*
import com.zaky.agrocare.data.Product
import com.zaky.agrocare.data.remote.FirebaseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel : ViewModel() {

    // Daftar produk hardcode sebagai cadangan
    private val hardcodedProducts = listOf(
        Product(1, "Tomat Merah Segar Organik", "Rp 25.000", "https://picsum.photos/seed/tomato/400/300"),
        Product(2, "Bibit Cabai Rawit Unggul", "Rp 15.000", "https://picsum.photos/seed/chili/400/300"),
        Product(3, "Pupuk Kompos Premium 5kg", "Rp 45.000", "https://picsum.photos/seed/fertilizer/400/300"),
        Product(4, "Bibit Padi Inpari 32", "Rp 85.000", "https://picsum.photos/seed/rice/400/300"),
        Product(5, "Pupuk Kandang Premium", "Rp 12.000", "https://picsum.photos/seed/sprayer/400/300"),
        Product(6, "Benih Jagung Hibrida", "Rp 60.000", "https://picsum.photos/seed/corn/400/300"),
        Product(7, "Pupuk Organik Cair (POC)", "Rp 40.000", "https://picsum.photos/seed/corn/400/300")
    )

    private val _allProducts = MutableLiveData<List<Product>>()
    val allProducts: LiveData<List<Product>> = _allProducts

    private val _homeProducts = MutableLiveData<List<Product>>()
    val homeProducts: LiveData<List<Product>> = _homeProducts

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            val dbEntities = FirebaseRepository.getAllProducts()
            val dbProducts = dbEntities.map { entity ->
                Product(
                    id = entity.id,
                    name = entity.name,
                    price = "Rp " + String.format(Locale("id", "ID"), "%,d", entity.price).replace(',', '.'),
                    imageUrl = entity.imageName
                )
            }
            
            // Gabungkan produk Hardcode dan Database
            val combinedProducts = hardcodedProducts + dbProducts
            
            _allProducts.value = combinedProducts
            _homeProducts.value = combinedProducts.take(4)
        }
    }
}
