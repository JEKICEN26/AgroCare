package com.zaky.agrocare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zaky.agrocare.R
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.databinding.FragmentHomeBinding
import com.zaky.agrocare.ui.cart.CartViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    // Gunakan activityViewModels agar ViewModel berbagi data dengan BottomSheet
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        
        setupRecyclerView()
        setupMenuListeners()

        homeViewModel.products.observe(viewLifecycleOwner) { products ->
            binding.rvProducts.adapter = ProductAdapter(
                products,
                onItemClick = { product ->
                    val bundle = Bundle().apply {
                        putInt("productId", product.id)
                        putString("productName", product.name)
                        putString("productPrice", product.price)
                        putString("productImage", product.imageUrl)
                    }
                    findNavController().navigate(R.id.navigation_product_detail, bundle)
                },
                onAddToCartClick = { product ->
                    // Konversi harga string ke Int (misal "Rp 15.000" -> 15000)
                    val priceInt = product.price.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                    cartViewModel.addToCart(
                        CartItem(
                            id = product.id,
                            name = product.name,
                            price = priceInt,
                            imageUrl = product.imageUrl,
                            quantity = 1
                        )
                    )
                    Toast.makeText(requireContext(), "Dimasukkan ke keranjang", Toast.LENGTH_SHORT).show()
                }
            )
        }
        
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.setHasFixedSize(true)
        binding.rvProducts.isNestedScrollingEnabled = false
    }

    private fun setupMenuListeners() {
        binding.menuEdukasi.setOnClickListener {
            findNavController().navigate(R.id.navigation_education_module)
        }

        binding.menuBibit.setOnClickListener {
            val bundle = Bundle().apply {
                putString("title", "Toko Bibit Unggul")
            }
            findNavController().navigate(R.id.navigation_product_category, bundle)
        }

        binding.menuPupuk.setOnClickListener {
            val bundle = Bundle().apply {
                putString("title", "Pupuk Organik Premium")
            }
            findNavController().navigate(R.id.navigation_product_category, bundle)
        }
        
        binding.tvSeeAll.setOnClickListener {
            val bundle = Bundle().apply {
                putString("title", "Semua Produk")
            }
            findNavController().navigate(R.id.navigation_product_category, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
