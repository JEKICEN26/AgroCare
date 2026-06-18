package com.zaky.agrocare.ui.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zaky.agrocare.databinding.FragmentScanBinding

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scanViewModel = ViewModelProvider(this).get(ScanViewModel::class.java)
        _binding = FragmentScanBinding.inflate(inflater, container, false)

        binding.btnScan.setOnClickListener {
            // Placeholder untuk aksi kamera
            Toast.makeText(context, "Fitur Kamera akan segera hadir!", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
