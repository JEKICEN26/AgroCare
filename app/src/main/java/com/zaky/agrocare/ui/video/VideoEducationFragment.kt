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
                id = "Pq407y-HMBg", // A random valid-looking ID for farming 
                title = "Cara Sukses Menanam Padi dengan Sistem Jajar Legowo",
                channel = "Kementerian Pertanian RI",
                description = "Pelajari metode menanam padi yang terbukti dapat meningkatkan hasil panen secara signifikan."
            ),
            VideoModule(
                id = "xZcT0-3Yg5c",
                title = "Panduan Lengkap Hidroponik untuk Pemula",
                channel = "Petani Millenial",
                description = "Langkah demi langkah memulai kebun hidroponik di pekarangan rumah dengan mudah dan murah."
            ),
            VideoModule(
                id = "y_Pz3p5Qv0o",
                title = "Pembuatan Pupuk Kompos Organik dari Limbah Dapur",
                channel = "Penyuluh Pertanian",
                description = "Cara mudah mengolah limbah rumah tangga menjadi pupuk berkualitas tinggi untuk tanaman anda."
            ),
            VideoModule(
                id = "R7kEa_k-21w",
                title = "Teknik Rahasia Menanam Cabai Rawit Lebat",
                channel = "AgroCare Official",
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
