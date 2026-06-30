package com.zaky.agrocare.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.zaky.agrocare.databinding.FragmentHelpDetailBinding

class HelpDetailFragment : Fragment() {

    private var _binding: FragmentHelpDetailBinding? = null
    private val binding get() = _binding!!

    // Mengambil argumen yang dikirim dari HelpCenter
    private val args: HelpDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        binding.tvHelpTitle.text = args.title
        binding.tvHelpContent.text = args.content

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
