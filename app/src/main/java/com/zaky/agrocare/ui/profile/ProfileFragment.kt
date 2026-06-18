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
import com.zaky.agrocare.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        setupPaymentSimulation()

        return binding.root
    }

    private fun setupPaymentSimulation() {
        // Set initial state colors
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        val colorGray = ContextCompat.getColor(requireContext(), R.color.colorTextSecondary)
        
        binding.ivStatusUnpaid.setColorFilter(colorPrimary)
        
        binding.llStatusUnpaid.setOnClickListener {
            Toast.makeText(requireContext(), "Memproses Pembayaran...", Toast.LENGTH_SHORT).show()
            it.postDelayed({ animateToPacked(colorPrimary, colorGray) }, 1500)
        }
    }

    private fun animateToPacked(colorPrimary: Int, colorGray: Int) {
        // Unpaid -> Packed
        binding.badgeUnpaid.visibility = View.GONE
        binding.tvSubLabelUnpaid.visibility = View.INVISIBLE
        binding.ivStatusUnpaid.setColorFilter(colorGray)

        binding.badgePacked.visibility = View.VISIBLE
        binding.badgePacked.text = "1"
        binding.tvSubLabelPacked.visibility = View.VISIBLE
        binding.tvSubLabelPacked.text = "1 Pesanan"
        binding.ivStatusPacked.setColorFilter(colorPrimary)
        
        binding.ivStatusPacked.animate().scaleX(1.3f).scaleY(1.3f).setDuration(200).withEndAction {
            binding.ivStatusPacked.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
        }.start()

        binding.root.postDelayed({ animateToSent(colorPrimary, colorGray) }, 2000)
    }

    private fun animateToSent(colorPrimary: Int, colorGray: Int) {
        // Packed -> Sent
        binding.badgePacked.visibility = View.GONE
        binding.tvSubLabelPacked.visibility = View.INVISIBLE
        binding.ivStatusPacked.setColorFilter(colorGray)

        binding.badgeSent.visibility = View.VISIBLE
        binding.badgeSent.text = "1"
        binding.tvSubLabelSent.visibility = View.VISIBLE
        binding.tvSubLabelSent.text = "1 Pesanan"
        binding.ivStatusSent.setColorFilter(colorPrimary)
        
        binding.ivStatusSent.animate().scaleX(1.3f).scaleY(1.3f).setDuration(200).withEndAction {
            binding.ivStatusSent.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
        }.start()

        binding.root.postDelayed({ animateToRate(colorPrimary, colorGray) }, 2000)
    }

    private fun animateToRate(colorPrimary: Int, colorGray: Int) {
        // Sent -> Rate
        binding.badgeSent.visibility = View.GONE
        binding.tvSubLabelSent.visibility = View.INVISIBLE
        binding.ivStatusSent.setColorFilter(colorGray)

        binding.badgeRate.visibility = View.VISIBLE
        binding.badgeRate.text = "1"
        binding.tvSubLabelRate.visibility = View.VISIBLE
        binding.tvSubLabelRate.text = "1 Pesanan"
        binding.ivStatusRate.setColorFilter(colorPrimary)
        
        binding.ivStatusRate.animate().scaleX(1.3f).scaleY(1.3f).setDuration(200).withEndAction {
            binding.ivStatusRate.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
        }.start()
        
        Toast.makeText(requireContext(), "Pesanan telah sampai! Silakan beri nilai.", Toast.LENGTH_LONG).show()
    }

    private fun setupMenuListeners() {
        // Navigation to Education Module
        binding.menuEducation.setOnClickListener {
            findNavController().navigate(R.id.navigation_video_education)
        }

        // Logout interaction
        binding.btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnSettings.setOnClickListener {
             Toast.makeText(requireContext(), "Pengaturan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
