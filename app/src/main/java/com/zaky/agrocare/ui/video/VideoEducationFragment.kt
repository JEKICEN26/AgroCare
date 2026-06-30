package com.zaky.agrocare.ui.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.databinding.FragmentVideoEducationBinding

class VideoEducationFragment : Fragment() {

    private var _binding: FragmentVideoEducationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoEducationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        val mockVideos = listOf(
            VideoModule(
                id = "jB6ozK-uFEI", // Video dari permintaan Anda
                title = "Panduan Pintar Bertani", // Anda bisa mengubah judul ini nanti
                channel = "DW Indonesia",
                description = "Pelajari berbagai metode dan panduan dasar pertanian dari video ini."
            ),
            VideoModule(
                id = "4IT4nsn2Kbk",
                title = "Panduan Lengkap Hidroponik untuk Pemula",
                channel = "TDA TV",
                description = "Langkah demi langkah memulai kebun hidroponik di pekarangan rumah dengan mudah dan murah."
            ),
            VideoModule(
                id = "jF_ja8ZT3MY",
                title = "Pembuatan Pupuk Kompos Organik dari Limbah Dapur",
                channel = "Yudhi green",
                description = "Cara mudah mengolah limbah rumah tangga menjadi pupuk berkualitas tinggi untuk tanaman anda."
            ),
            VideoModule(
                id = "NRWBcHl3EGc",
                title = "Teknik Rahasia Menanam Cabai Rawit Lebat",
                channel = "PORTAL INFORMASI NASIONAL",
                description = "Simak rahasia petani sukses membuat tanaman cabai berbuah sangat lebat dan anti hama."
            )
        )
        
        val adapter = VideoModuleAdapter(mockVideos)
        binding.rvVideoModules.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
