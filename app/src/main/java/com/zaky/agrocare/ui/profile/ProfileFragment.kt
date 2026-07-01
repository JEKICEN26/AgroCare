package com.zaky.agrocare.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.R
import com.zaky.agrocare.data.OrderManager
import com.zaky.agrocare.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private var resetClickCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        profileViewModel.userName.observe(viewLifecycleOwner) {
            binding.tvProfileName.text = it
        }

        setupMenuListeners()
        setupOrderNavigation()

        // Observe perubahan data pesanan secara live untuk update badge
        OrderManager.orders.observe(viewLifecycleOwner) {
            updateBadges()
        }

        return binding.root
    }

    private fun updateBadges() {
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        val colorGray = ContextCompat.getColor(requireContext(), R.color.colorTextSecondary)

        // Belum Bayar
        val unpaidCount = OrderManager.getCountByStatus(1)
        if (unpaidCount > 0) {
            binding.badgeUnpaid.visibility = View.VISIBLE
            binding.badgeUnpaid.text = unpaidCount.toString()
            binding.tvSubLabelUnpaid.visibility = View.VISIBLE
            binding.tvSubLabelUnpaid.text = "$unpaidCount Pesanan"
            binding.ivStatusUnpaid.setColorFilter(colorPrimary)
        } else {
            binding.badgeUnpaid.visibility = View.GONE
            binding.tvSubLabelUnpaid.visibility = View.INVISIBLE
            binding.ivStatusUnpaid.setColorFilter(colorGray)
        }

        // Dikemas
        val packedCount = OrderManager.getCountByStatus(2)
        if (packedCount > 0) {
            binding.badgePacked.visibility = View.VISIBLE
            binding.badgePacked.text = packedCount.toString()
            binding.tvSubLabelPacked.visibility = View.VISIBLE
            binding.tvSubLabelPacked.text = "$packedCount Pesanan"
            binding.ivStatusPacked.setColorFilter(colorPrimary)
        } else {
            binding.badgePacked.visibility = View.GONE
            binding.tvSubLabelPacked.visibility = View.INVISIBLE
            binding.ivStatusPacked.setColorFilter(colorGray)
        }

        // Dikirim
        val sentCount = OrderManager.getCountByStatus(3)
        if (sentCount > 0) {
            binding.badgeSent.visibility = View.VISIBLE
            binding.badgeSent.text = sentCount.toString()
            binding.tvSubLabelSent.visibility = View.VISIBLE
            binding.tvSubLabelSent.text = "$sentCount Pesanan"
            binding.ivStatusSent.setColorFilter(colorPrimary)
        } else {
            binding.badgeSent.visibility = View.GONE
            binding.tvSubLabelSent.visibility = View.INVISIBLE
            binding.ivStatusSent.setColorFilter(colorGray)
        }

        // Beri Nilai (count pesanan selesai yang belum di-rating)
        val unratedCount = OrderManager.getUnratedFinishedCount()
        if (unratedCount > 0) {
            binding.badgeRate.visibility = View.VISIBLE
            binding.badgeRate.text = unratedCount.toString()
            binding.tvSubLabelRate.visibility = View.VISIBLE
            binding.tvSubLabelRate.text = "$unratedCount Pesanan"
            binding.ivStatusRate.setColorFilter(colorPrimary)
        } else {
            binding.badgeRate.visibility = View.GONE
            binding.tvSubLabelRate.visibility = View.INVISIBLE
            binding.ivStatusRate.setColorFilter(colorGray)
        }
    }

    private fun setupOrderNavigation() {
        val navController = findNavController()

        // Fungsi helper untuk menavigasi ke tab spesifik
        fun navigateToOrders(tabIndex: Int) {
            val bundle = Bundle().apply {
                putInt("initialTab", tabIndex)
            }
            navController.navigate(R.id.navigation_my_orders, bundle)
        }

        // Klik "Pesanan Saya" (Lihat Semua)
        binding.cvOrders.setOnClickListener {
            navigateToOrders(0) // Tab: Semua
        }

        // Klik "Belum Bayar"
        binding.llStatusUnpaid.setOnClickListener {
            navigateToOrders(1) // Tab: Belum Bayar
        }

        // Klik "Dikemas"
        binding.llStatusPacked.setOnClickListener {
            navigateToOrders(2) // Tab: Dikemas
        }

        // Klik "Dikirim"
        binding.llStatusSent.setOnClickListener {
            navigateToOrders(3) // Tab: Dikirim
        }

        // Klik "Beri Nilai"
        binding.llStatusRate.setOnClickListener {
            navigateToOrders(4) // Tab: Selesai
        }
    }

    private fun setupMenuListeners() {
        // Navigation to Wishlist
        binding.menuWishlist.setOnClickListener {
            findNavController().navigate(R.id.navigation_favorite)
        }
        
        // Navigation to Help Center
        binding.menuHelpCenter.setOnClickListener {
            findNavController().navigate(R.id.navigation_help_center)
        }

        // Navigation to Education Module
        binding.menuEducation.setOnClickListener {
            findNavController().navigate(R.id.navigation_video_education)
        }

        // Logout interaction
        binding.btnLogout.setOnClickListener {
            val sessionManager = com.zaky.agrocare.utils.SessionManager(requireContext())
            sessionManager.logout()
            Toast.makeText(requireContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            
            val intent = android.content.Intent(requireContext(), com.zaky.agrocare.ui.login.LoginActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        

        
        // Fitur rahasia (Easter Egg) untuk mereset simulasi data
        binding.chipVendor.setOnClickListener {
            resetClickCount++
            if (resetClickCount >= 5) {
                OrderManager.resetData()
                val cartViewModel = ViewModelProvider(requireActivity()).get(com.zaky.agrocare.ui.cart.CartViewModel::class.java)
                cartViewModel.clearCart()
                
                Toast.makeText(requireContext(), "Data Simulasi Direset ke Awal!", Toast.LENGTH_SHORT).show()
                resetClickCount = 0
            } else {
                val remaining = 5 - resetClickCount
                Toast.makeText(requireContext(), "Ketuk $remaining kali lagi untuk mereset simulasi data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
