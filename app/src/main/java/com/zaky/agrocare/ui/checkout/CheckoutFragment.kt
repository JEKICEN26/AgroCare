package com.zaky.agrocare.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.R
import com.zaky.agrocare.data.AddressManager
import com.zaky.agrocare.data.OrderManager
import com.zaky.agrocare.data.local.AddressEntity
import com.zaky.agrocare.databinding.FragmentCheckoutBinding
import com.zaky.agrocare.ui.cart.CartViewModel
import com.zaky.agrocare.utils.toRupiah

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    
    private var currentAddress: AddressEntity? = null
    
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

    override fun onResume() {
        super.onResume()
        loadDefaultAddress()
    }

    private fun loadDefaultAddress() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val address = AddressManager.getDefaultAddress()
            currentAddress = address
            if (address != null) {
                binding.tvCheckoutAddressTitle.text = address.title
                binding.tvCheckoutAddressDetail.text = address.fullAddress
                binding.btnChangeAddress.text = "Ubah Alamat"
            } else {
                binding.tvCheckoutAddressTitle.text = "Belum ada alamat"
                binding.tvCheckoutAddressDetail.text = "Silakan tambah atau pilih alamat pengiriman"
                binding.btnChangeAddress.text = "Pilih Alamat"
            }
        }
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
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnChangeAddress.setOnClickListener {
            findNavController().navigate(R.id.navigation_delivery_address)
        }

        binding.btnFinishCheckout.setOnClickListener {
            val address = currentAddress?.fullAddress
            
            // Validasi Alamat
            if (address.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Silakan pilih alamat pengiriman terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil item keranjang saat ini dan buat pesanan nyata
            val cartItems = viewModel.cartItems.value
            if (!cartItems.isNullOrEmpty()) {
                OrderManager.addOrderFromCart(cartItems, address)
            }

            // Tampilkan Dialog Sukses
            showSuccessDialog()
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Pesanan Berhasil Dibuat!")
            .setMessage("Pesanan Anda telah masuk ke daftar pesanan dengan status 'Belum Bayar'. Silakan lanjutkan pembayaran di halaman Pesanan Saya.")
            .setPositiveButton("Lihat Pesanan") { _, _ ->
                // Tambahkan notifikasi
                com.zaky.agrocare.ui.notifications.NotificationManager.addNotification(
                    requireContext(),
                    com.zaky.agrocare.ui.notifications.NotificationItem(
                        title = "Pesanan Berhasil Dibuat!",
                        description = "Pesanan Anda telah dibuat. Silakan selesaikan pembayaran di halaman Pesanan Saya.",
                        timestamp = "Baru saja"
                    )
                )
                // Bersihkan keranjang setelah checkout berhasil
                viewModel.clearCart()
                // Navigasi ke Pesanan Saya tab Belum Bayar
                val bundle = Bundle().apply {
                    putInt("initialTab", 1) // Tab: Belum Bayar
                }
                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, false)
                    .build()
                findNavController().navigate(R.id.navigation_my_orders, bundle, navOptions)
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
