package com.zaky.agrocare.ui.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zaky.agrocare.data.local.AddressEntity
import com.zaky.agrocare.databinding.ItemAddressBinding

class AddressAdapter(
    private var addresses: List<AddressEntity>,
    private val onSetDefaultClick: (AddressEntity) -> Unit,
    private val onDeleteClick: (AddressEntity) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    fun updateData(newAddresses: List<AddressEntity>) {
        this.addresses = newAddresses
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addresses[position])
    }

    override fun getItemCount(): Int = addresses.size

    inner class AddressViewHolder(private val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: AddressEntity) {
            binding.tvAddressTitle.text = address.title
            binding.tvFullAddress.text = address.fullAddress

            if (address.isDefault) {
                binding.chipDefault.visibility = View.VISIBLE
                binding.btnSetDefault.visibility = View.GONE
                // Tidak boleh hapus alamat utama jika itu satu-satunya, 
                // tapi untuk simplifikasi kita biarkan tombol delete
            } else {
                binding.chipDefault.visibility = View.GONE
                binding.btnSetDefault.visibility = View.VISIBLE
            }

            binding.btnSetDefault.setOnClickListener {
                onSetDefaultClick(address)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(address)
            }
        }
    }
}
