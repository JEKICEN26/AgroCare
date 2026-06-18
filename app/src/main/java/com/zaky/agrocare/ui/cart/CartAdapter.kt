package com.zaky.agrocare.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.databinding.ItemCartBinding
import com.zaky.agrocare.utils.toRupiah

class CartAdapter(
    private val onIncrease: (Int) -> Unit,
    private val onDecrease: (Int) -> Unit,
    private val onRemove: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            tvCartItemTitle.text = item.name
            tvCartItemPrice.text = (item.price * item.quantity).toRupiah()
            tvQuantity.text = item.quantity.toString()
            
            ivCartItem.load(item.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }

            btnPlus.setOnClickListener { onIncrease(item.id) }
            btnMinus.setOnClickListener { onDecrease(item.id) }
            btnRemoveItem.setOnClickListener { onRemove(item.id) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
