package com.zaky.agrocare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zaky.agrocare.data.Product

class HomeViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Product>>().apply {
        value = listOf(
            Product(1, "Tomat Merah Segar Organik", "Rp 25.000", "https://picsum.photos/seed/tomato/400/300"),
            Product(2, "Bibit Cabai Rawit Unggul", "Rp 15.000", "https://picsum.photos/seed/chili/400/300"),
            Product(3, "Pupuk Kompos Premium 5kg", "Rp 45.000", "https://picsum.photos/seed/fertilizer/400/300"),
            Product(4, "Bibit Padi Inpari 32", "Rp 85.000", "https://picsum.photos/seed/rice/400/300"),
            Product(5, "Alat Semprot Hama 2L", "Rp 120.000", "https://picsum.photos/seed/sprayer/400/300"),
            Product(6, "Benih Jagung Hibrida", "Rp 60.000", "https://picsum.photos/seed/corn/400/300")
        )
    }
    val products: LiveData<List<Product>> = _products
}
