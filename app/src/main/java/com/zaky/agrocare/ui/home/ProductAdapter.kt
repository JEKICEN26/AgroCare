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
    private val onAddToCartClick: (Product) -> Unit
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
