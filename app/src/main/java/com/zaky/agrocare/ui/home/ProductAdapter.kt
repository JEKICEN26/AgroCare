package com.zaky.agrocare.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zaky.agrocare.data.Product
import com.zaky.agrocare.databinding.ItemProductBinding

class ProductAdapter(
    private val listProduct: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit,
    private val isFavorite: (Product) -> Boolean = { false },
    private val onFavoriteClick: (Product) -> Unit = {}
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = listProduct[position]
        holder.binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = product.price
            
            // Logika untuk memuat gambar dari URL atau Drawable lokal
            val context = root.context
            val imageSource: Any = if (product.imageUrl.startsWith("http")) {
                product.imageUrl
            } else {
                // Mencari ID drawable berdasarkan nama string
                val resId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
                if (resId != 0) resId else android.R.drawable.ic_menu_gallery
            }

            ivProduct.load(imageSource) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
            
            // Handle Favorite state
            val isFav = isFavorite(product)
            if (isFav) {
                btnFavorite.setImageResource(com.zaky.agrocare.R.drawable.ic_heart_filled)
                btnFavorite.setColorFilter(android.graphics.Color.parseColor("#F44336"))
            } else {
                btnFavorite.setImageResource(com.zaky.agrocare.R.drawable.ic_heart_outline)
                btnFavorite.setColorFilter(android.graphics.Color.parseColor("#757575"))
            }
            
            btnFavorite.setOnClickListener {
                onFavoriteClick(product)
            }
            
            root.setOnClickListener {
                onItemClick(product)
            }
            
            btnAddToCart.setOnClickListener {
                onAddToCartClick(product)
            }
        }
    }

    override fun getItemCount(): Int = listProduct.size
}
