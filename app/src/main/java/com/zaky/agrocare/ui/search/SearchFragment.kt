package com.zaky.agrocare.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.R
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchAdapter: SearchResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel dengan Factory secara eksplisit untuk mencegah NoSuchMethodException
        val database = AppDatabase.getDatabase(requireContext().applicationContext, viewLifecycleOwner.lifecycleScope)
        val factory = SearchViewModelFactory(database.productDao())
        searchViewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        setupUI()
        setupRecyclerView()
        observeViewModel()

        // Munculkan keyboard otomatis saat halaman dibuka
        binding.etSearchInput.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearchInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupUI() {
        binding.btnBackSearch.setOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }

        binding.btnClearSearch.setOnClickListener {
            binding.etSearchInput.text?.clear()
        }

        binding.etSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                binding.btnClearSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                searchViewModel.search(query)
            }
        })

        binding.etSearchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchResultAdapter(
            onProductClick = { product ->
                hideKeyboard()
                val bundle = Bundle().apply {
                    putInt("productId", product.id)
                    putString("productName", product.name)
                    putString("productPrice", product.price)
                    putString("productImage", product.imageUrl)
                }
                findNavController().navigate(R.id.navigation_product_detail, bundle)
            },
            onEducationClick = { module ->
                hideKeyboard()
                Toast.makeText(requireContext(), "Membuka Modul: ${module.title}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvSearchResults.adapter = searchAdapter
    }

    private fun observeViewModel() {
        searchViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (binding.etSearchInput.text.isNullOrBlank()) {
                binding.rvSearchResults.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.tvEmptyMessage.text = "Cari produk atau artikel edukasi"
                binding.ivEmptyIcon.setImageResource(android.R.drawable.ic_menu_search)
            } else if (results.isEmpty()) {
                binding.rvSearchResults.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.tvEmptyMessage.text = "Pencarian tidak ditemukan"
                binding.ivEmptyIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                binding.rvSearchResults.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
                searchAdapter.submitList(results)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearchInput.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
