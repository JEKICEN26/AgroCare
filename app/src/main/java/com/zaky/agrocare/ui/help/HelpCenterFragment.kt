package com.zaky.agrocare.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.databinding.FragmentHelpCenterBinding

class HelpCenterFragment : Fragment() {

    private var _binding: FragmentHelpCenterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpCenterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActions()
    }

    private fun setupActions() {
        // Back Navigation
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Topics
        binding.topicAccount.setOnClickListener {
            val action = HelpCenterFragmentDirections.actionHelpCenterToDetail("Akun Saya", "Pengaturan profil, keamanan, dan informasi pribadi akun Anda dapat dikelola melalui tab Profil. Pastikan selalu menggunakan password yang kuat.")
            findNavController().navigate(action)
        }
        binding.topicOrder.setOnClickListener {
            val action = HelpCenterFragmentDirections.actionHelpCenterToDetail("Pesanan", "Semua riwayat pesanan Anda, termasuk status 'Belum Bayar', 'Dikemas', dan 'Dikirim' dapat dilihat pada halaman Profil di bagian Pesanan Saya.")
            findNavController().navigate(action)
        }
        binding.topicShipping.setOnClickListener {
            val action = HelpCenterFragmentDirections.actionHelpCenterToDetail("Pengiriman", "Estimasi pengiriman bergantung pada kurir yang dipilih. Pastikan alamat pengiriman sudah benar sebelum melakukan checkout.")
            findNavController().navigate(action)
        }
        binding.topicPayment.setOnClickListener {
            val action = HelpCenterFragmentDirections.actionHelpCenterToDetail("Pembayaran", "Tersedia berbagai metode pembayaran seperti Transfer Bank, E-Wallet (OVO, GoPay, dll), dan layanan Cash On Delivery (COD) khusus untuk wilayah tertentu.")
            findNavController().navigate(action)
        }

        // FAQs
        binding.faq1.setOnClickListener {
            val action = HelpCenterFragmentDirections.actionHelpCenterToDetail("Melacak Pesanan", "Anda dapat melacak pesanan melalui menu 'Pesanan Saya' di halaman Profil. Klik pada pesanan yang sedang 'Dikirim' untuk melihat status resi pengiriman terkini dari kurir.")
            findNavController().navigate(action)
        }
        binding.faq2.setOnClickListener {
            val action = HelpCenterFragmentDirections.actionHelpCenterToDetail("Metode Pembayaran", "Saat melakukan Checkout, Anda bisa memilih metode pembayaran di bagian 'Pilih Metode'. Instruksi pembayaran akan muncul setelah pesanan dibuat.")
            findNavController().navigate(action)
        }
        binding.faq3.setOnClickListener {
            val action = HelpCenterFragmentDirections.actionHelpCenterToDetail("Pengembalian Barang", "Barang yang rusak atau tidak sesuai dapat dikembalikan maksimal 2x24 jam setelah barang diterima. Anda wajib menyertakan video unboxing sebagai bukti pengembalian.")
            findNavController().navigate(action)
        }

        // Contact Buttons
        binding.btnLiveChat.setOnClickListener {
            val action = HelpCenterFragmentDirections.actionHelpCenterToChat()
            findNavController().navigate(action)
        }
        binding.btnPhone.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
            intent.data = android.net.Uri.parse("tel:081234567890")
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
