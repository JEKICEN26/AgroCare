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
            ivProduct.load(product.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }
            
            // Handle Favorite state
            val isFav = isFavorite(product)
            if (isFav) {
                btnFavorite.setImageResource(com.zaky.agrocare.R.drawable.ic_heart_filled)
                btnFavorite.setColorFilter(android.graphics.Color.parseColor("#F44336")) // Red tint for favorite
            } else {
                btnFavorite.setImageResource(com.zaky.agrocare.R.drawable.ic_heart_outline)
                btnFavorite.setColorFilter(android.graphics.Color.parseColor("#757575")) // Gray tint for non-favorite
            }
            
            btnFavorite.setOnClickListener {
                onFavoriteClick(product)
                if (!isFav) {
                    android.widget.Toast.makeText(it.context, "Berhasil ditambahkan ke Favorit Saya", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(it.context, "Berhasil dihapus dari Favorit Saya", android.widget.Toast.LENGTH_SHORT).show()
                }
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
