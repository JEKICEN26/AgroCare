package com.zaky.agrocare.ui.search

import androidx.lifecycle.*
import com.zaky.agrocare.data.Product
import com.zaky.agrocare.data.SearchItem
import com.zaky.agrocare.ui.education.EducationModule
import com.zaky.agrocare.data.remote.FirebaseRepository
import kotlinx.coroutines.launch
import java.util.Locale

class SearchViewModel : ViewModel() {

    // Daftar produk hardcode sinkron dengan HomeViewModel
    private val hardcodedProducts = listOf(
        Product(1, "Tomat Merah Segar Organik", "Rp 25.000", "https://picsum.photos/seed/tomato/400/300"),
        Product(2, "Bibit Cabai Rawit Unggul", "Rp 15.000", "https://picsum.photos/seed/chili/400/300"),
        Product(3, "Pupuk Kompos Premium 5kg", "Rp 45.000", "https://picsum.photos/seed/fertilizer/400/300"),
        Product(4, "Bibit Padi Inpari 32", "Rp 85.000", "https://picsum.photos/seed/rice/400/300"),
        Product(5, "Pupuk Kandang Premium", "Rp 12.000", "https://picsum.photos/seed/sprayer/400/300"),
        Product(6, "Benih Jagung Hibrida", "Rp 60.000", "https://picsum.photos/seed/corn/400/300"),
        Product(7, "Pupuk Organik Cair (POC)", "Rp 40.000", "https://picsum.photos/seed/corn/400/300")
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

        viewModelScope.launch {
            val lowerQuery = query.lowercase()
            val resultList = mutableListOf<SearchItem>()

            // Ambil data dari Firebase Firestore
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
            val allCombinedProducts = hardcodedProducts + dbProducts

            // 1. Cari Produk
            val matchedProducts = allCombinedProducts.filter {
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
}
