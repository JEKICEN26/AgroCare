package com.zaky.agrocare.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

        return binding.root
    }

    private fun setupMenuListeners() {
        // Navigation to Education Module
        binding.menuEducation.setOnClickListener {
            // Ensure you have added EducationModuleFragment to your nav_graph.xml 
            // with an action or use the ID directly if using a standard naming convention
            // findNavController().navigate(R.id.navigation_education_module)
            
            Toast.makeText(requireContext(), "Membuka Modul Edukasi...", Toast.LENGTH_SHORT).show()
        }

        // Logout interaction
        binding.btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            // Add your logout logic here (e.g., clear session and navigate to Login)
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
