package com.zaky.agrocare.ui.home

import androidx.lifecycle.*
import com.zaky.agrocare.data.Product
import com.zaky.agrocare.data.local.ProductDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val dao: ProductDao) : ViewModel() {

    private val hardcodedProducts = listOf(
        Product(1, "Tomat Merah Segar Organik", "Rp 25.000", "https://picsum.photos/seed/tomato/400/300"),
        Product(2, "Bibit Cabai Rawit Unggul", "Rp 15.000", "https://picsum.photos/seed/chili/400/300"),
        Product(3, "Pupuk Kompos Premium 5kg", "Rp 45.000", "https://picsum.photos/seed/fertilizer/400/300"),
        Product(4, "Bibit Padi Inpari 32", "Rp 85.000", "https://picsum.photos/seed/rice/400/300"),
        Product(5, "Pupuk Kandang Premium", "Rp 12.000", "https://picsum.photos/seed/sprayer/400/300"),
        Product(6, "Benih Jagung Hibrida", "Rp 60.000", "https://picsum.photos/seed/corn/400/300"),
        Product(7, "Pupuk Organik Cair (POC)", "Rp 40.000", "https://picsum.photos/seed/corn/400/300")
    )

    // Semua produk (Hardcode + Database)
    val allProducts: LiveData<List<Product>> = dao.getAllProducts().map { entities ->
        val dbProducts = entities.map { entity ->
            Product(
                id = entity.id,
                name = entity.name,
                price = "Rp " + String.format("%,d", entity.price).replace(',', '.'),
                imageUrl = entity.imageName
            )
        }
        hardcodedProducts + dbProducts
    }.asLiveData()

    // Produk untuk tampilan Home (Dibatasi 4 saja)
    val homeProducts: LiveData<List<Product>> = allProducts.map { it.take(4) }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getAllProducts().first()
        }
    }
}

class HomeViewModelFactory(private val dao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
