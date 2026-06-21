package com.zaky.agrocare.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.R
import com.zaky.agrocare.databinding.FragmentCheckoutBinding
import com.zaky.agrocare.ui.cart.CartViewModel
import com.zaky.agrocare.utils.toRupiah

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    
    // Inisialisasi SharedViewModel menggunakan activityViewModels()
    private val viewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCourierDropdown()
        observeViewModel()
        setupListeners()
    }

    private fun setupCourierDropdown() {
        val couriers = listOf("AgroExpress (Instant 2 Jam)")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, couriers)
        binding.acCourier.setAdapter(adapter)
    }

    private fun observeViewModel() {
        // Observasi totalPrice untuk menampilkan di bagian bawah
        viewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            binding.tvTotalCheckout.text = total.toRupiah()
        }
    }

    private fun setupListeners() {
        binding.btnFinishCheckout.setOnClickListener {
            val address = binding.etAddress.text.toString()
            
            // Validasi Alamat
            if (address.isEmpty()) {
                binding.etAddress.error = "Alamat tidak boleh kosong"
                return@setOnClickListener
            }

            // Tampilkan Dialog Sukses
            showSuccessDialog()
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Pembayaran Berhasil!")
            .setMessage("Pesanan Anda sedang diproses.")
            .setPositiveButton("OK") { _, _ ->
                // Tambahkan notifikasi
                com.zaky.agrocare.ui.notifications.NotificationManager.addNotification(
                    com.zaky.agrocare.ui.notifications.NotificationItem(
                        title = "Pembayaran Berhasil!",
                        description = "Pembayaran pesanan Anda telah berhasil dikonfirmasi. Pesanan sedang disiapkan.",
                        timestamp = "Baru saja"
                    )
                )
                // Bersihkan keranjang setelah checkout berhasil
                viewModel.clearCart()
                // Kembali ke Home
                findNavController().popBackStack(R.id.navigation_home, false)
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
