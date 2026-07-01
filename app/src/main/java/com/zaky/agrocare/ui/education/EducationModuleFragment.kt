package com.zaky.agrocare.ui.education

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.databinding.FragmentGrafikprediksiBinding

class EducationModuleFragment : Fragment() {

    private var _binding: FragmentGrafikprediksiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGrafikprediksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupWebView()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webView = binding.webView
        
        // Aktifkan JavaScript (dibutuhkan oleh Streamlit)
        webView.settings.javaScriptEnabled = true
        // Aktifkan DOM Storage (dibutuhkan oleh web app modern/Streamlit)
        webView.settings.domStorageEnabled = true
        
        // Mencegah halaman dibuka di browser eksternal
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        
        // Load URL Streamlit
        webView.loadUrl("https://predictwebagrocare-5az5qs8nvrkkgqdhrhjpqs.streamlit.app/")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
