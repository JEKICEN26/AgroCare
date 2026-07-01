package com.zaky.agrocare.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zaky.agrocare.R
import com.zaky.agrocare.data.Order
import com.zaky.agrocare.databinding.ItemOrderBinding
import com.zaky.agrocare.utils.loadProductImage
import com.zaky.agrocare.utils.toRupiah

class OrderAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit,
    private val onActionClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position], onItemClick, onActionClick)
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(order: Order, onItemClick: (Order) -> Unit, onActionClick: (Order) -> Unit) {
            binding.root.setOnClickListener {
                onItemClick(order)
            }
            binding.tvStoreName.text = order.storeName
            binding.tvOrderStatus.text = order.status
            binding.tvProductName.text = order.productName
            binding.tvProductPriceQuantity.text = "${order.price.toRupiah()} x ${order.quantity}"
            binding.tvTotalAmount.text = order.totalAmount.toRupiah()

            // Setup Image with Coil Extension
            binding.ivProductImage.loadProductImage(order.imageUrl)

            val context = binding.root.context
            
            // Status Styling and Button Actions
            when (order.statusId) {
                1 -> { // Belum Bayar
                    binding.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorAccentOrange))
                    binding.btnOrderAction.text = "Bayar Sekarang"
                    binding.btnOrderAction.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentOrange))
                }
                2 -> { // Dikemas
                    binding.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    binding.btnOrderAction.text = "Hubungi Penjual"
                    binding.btnOrderAction.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
                }
                3 -> { // Dikirim
                    binding.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    binding.btnOrderAction.text = "Lacak Pesanan"
                    binding.btnOrderAction.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                }
                4 -> { // Selesai
                    binding.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    if (order.isRated) {
                        // Sudah pernah di-rating, tombol berubah jadi Beli Lagi
                        binding.btnOrderAction.text = "Beli Lagi"
                        binding.btnOrderAction.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
                    } else {
                        binding.btnOrderAction.text = "Beri Penilaian"
                        binding.btnOrderAction.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    }
                }
                5 -> { // Dibatalkan
                    binding.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
                    binding.btnOrderAction.text = "Beli Lagi"
                    binding.btnOrderAction.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
                }
            }

            binding.btnOrderAction.setOnClickListener {
                onActionClick(order)
            }
        }
    }
}
