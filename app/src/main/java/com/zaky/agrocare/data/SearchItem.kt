package com.zaky.agrocare.data

sealed class SearchItem {
    data class Header(val title: String) : SearchItem()
    data class ProductResult(val product: Product) : SearchItem()
    data class EducationResult(val module: com.zaky.agrocare.ui.education.EducationModule) : SearchItem()
}
