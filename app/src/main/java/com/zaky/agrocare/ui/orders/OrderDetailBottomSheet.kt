package com.zaky.agrocare.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zaky.agrocare.data.Order
import com.zaky.agrocare.databinding.FragmentOrderDetailBottomSheetBinding
import com.zaky.agrocare.utils.loadProductImage
import java.text.NumberFormat
import java.util.Locale

class OrderDetailBottomSheet(
    private val order: Order,
    private val onCancelOrder: (Order) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentOrderDetailBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvProductName.text = order.productName
        binding.tvStoreName.text = order.storeName
        binding.tvPriceQuantity.text = "${formatRupiah(order.price)} x ${order.quantity}"
        binding.tvTotalAmount.text = formatRupiah(order.totalAmount)
        binding.tvAddress.text = if (order.address.isNotBlank()) order.address else "Alamat tidak tersedia"
        binding.tvPaymentMethod.text = if (order.paymentMethod.isNotBlank()) order.paymentMethod else "Belum Dibayar"

        binding.ivProductImage.loadProductImage(order.imageUrl)

        // Tampilkan tombol batal jika statusnya Belum Bayar (1) atau Dikemas (2)
        if (order.statusId == 1 || order.statusId == 2) {
            binding.btnCancelOrder.visibility = View.VISIBLE
            binding.btnCancelOrder.setOnClickListener {
                onCancelOrder(order)
                dismiss()
            }
        } else {
            binding.btnCancelOrder.visibility = View.GONE
        }
    }

    private fun formatRupiah(number: Int): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatRupiah.format(number)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
