package com.zaky.agrocare.ui.education

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.databinding.FragmentEducationModuleBinding

class EducationModuleFragment : Fragment() {

    private var _binding: FragmentEducationModuleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEducationModuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupRecyclerView()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        val dummyData = listOf(
            EducationModule(
                1,
                "Teknik Irigasi Modern untuk Padi",
                "Pelajari cara mengoptimalkan penggunaan air untuk meningkatkan hasil panen padi secara signifikan.",
                "Pertanian Dasar"
            ),
            EducationModule(
                2,
                "Pengendalian Hama Organik",
                "Metode ramah lingkungan untuk menjaga tanaman Anda dari serangan hama tanpa bahan kimia berbahaya.",
                "Hama & Penyakit"
            ),
            EducationModule(
                3,
                "Manajemen Nutrisi Hidroponik",
                "Panduan lengkap mencampur nutrisi AB Mix untuk berbagai jenis tanaman sayuran daun.",
                "Hidroponik"
            )
        )

        val adapter = EducationAdapter(dummyData)
        binding.rvEducationModules.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
