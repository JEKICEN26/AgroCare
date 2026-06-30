package com.zaky.agrocare.utils

import android.widget.ImageView
import coil.load
import java.text.NumberFormat
import java.util.Locale

fun Int.toRupiah(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(this).replace("Rp", "Rp ")
}

fun ImageView.loadProductImage(url: String?) {
    if (url.isNullOrEmpty()) {
        this.load(android.R.drawable.ic_menu_gallery)
        return
    }

    val context = this.context
    val imageSource: Any = if (url.startsWith("http")) {
        url
    } else {
        // Mencari ID drawable berdasarkan nama string (misal: "img_tomat")
        val resId = context.resources.getIdentifier(url, "drawable", context.packageName)
        if (resId != 0) resId else android.R.drawable.ic_menu_gallery
    }

    this.load(imageSource) {
        crossfade(true)
        placeholder(android.R.drawable.ic_menu_gallery)
        error(android.R.drawable.ic_menu_report_image)
    }
}
