package com.zaky.agrocare.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zaky.agrocare.data.Product
import com.zaky.agrocare.data.SearchItem
import com.zaky.agrocare.ui.education.EducationModule

class SearchViewModel : ViewModel() {

    // Dummy data sumber (gabungan dari HomeViewModel, ProductCategory, dan EducationModule)
    private val allProducts = listOf(
        Product(1, "Tomat Merah Segar Organik", "Rp 25.000", "https://picsum.photos/seed/tomato/400/300"),
        Product(2, "Bibit Cabai Rawit Unggul", "Rp 15.000", "https://picsum.photos/seed/chili/400/300"),
        Product(3, "Pupuk Kompos Premium 5kg", "Rp 45.000", "https://picsum.photos/seed/fertilizer/400/300"),
        Product(4, "Bibit Padi Inpari 32", "Rp 85.000", "https://picsum.photos/seed/rice/400/300"),
        Product(5, "Alat Semprot Hama 2L", "Rp 120.000", "https://picsum.photos/seed/sprayer/400/300"),
        Product(6, "Benih Jagung Hibrida", "Rp 60.000", "https://picsum.photos/seed/corn/400/300"),
        Product(7, "Benih Tomat Unggul", "Rp 15.000", "https://picsum.photos/seed/tomat/400/300"),
        Product(8, "Bibit Selada Hidroponik", "Rp 10.000", "https://picsum.photos/seed/selada/400/300"),
        Product(9, "Pupuk Organik Cair", "Rp 45.000", "https://picsum.photos/seed/pupuk1/400/300"),
        Product(10, "Pupuk NPK Mutiara", "Rp 35.000", "https://picsum.photos/seed/pupuk2/400/300"),
        Product(11, "Kompos Kambing Matang", "Rp 15.000", "https://picsum.photos/seed/pupuk3/400/300"),
        Product(12, "Bio-Aktivator EM4", "Rp 25.000", "https://picsum.photos/seed/pupuk4/400/300")
    )

    private val allEducationModules = listOf(
        EducationModule(1, "Teknik Irigasi Modern untuk Padi", "Pelajari cara mengoptimalkan penggunaan air untuk meningkatkan hasil panen padi secara signifikan.", "Pertanian Dasar"),
        EducationModule(2, "Pengendalian Hama Organik", "Metode ramah lingkungan untuk menjaga tanaman Anda dari serangan hama tanpa bahan kimia berbahaya.", "Hama & Penyakit"),
        EducationModule(3, "Manajemen Nutrisi Hidroponik", "Panduan lengkap mencampur nutrisi AB Mix untuk berbagai jenis tanaman sayuran daun.", "Hidroponik")
    )

    private val _searchResults = MutableLiveData<List<SearchItem>>()
    val searchResults: LiveData<List<SearchItem>> = _searchResults

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        val lowerQuery = query.lowercase()
        val resultList = mutableListOf<SearchItem>()

        // 1. Cari Produk
        val matchedProducts = allProducts.filter {
            it.name.lowercase().contains(lowerQuery)
        }

        if (matchedProducts.isNotEmpty()) {
            resultList.add(SearchItem.Header("Produk Terkait"))
            matchedProducts.forEach { product ->
                resultList.add(SearchItem.ProductResult(product))
            }
        }

        // 2. Cari Edukasi
        val matchedEducation = allEducationModules.filter {
            it.title.lowercase().contains(lowerQuery) || it.description.lowercase().contains(lowerQuery)
        }

        if (matchedEducation.isNotEmpty()) {
            resultList.add(SearchItem.Header("Modul Edukasi Terkait"))
            matchedEducation.forEach { module ->
                resultList.add(SearchItem.EducationResult(module))
            }
        }

        _searchResults.value = resultList
    }
}
