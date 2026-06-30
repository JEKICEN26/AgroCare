package com.zaky.agrocare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zaky.agrocare.R
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.databinding.FragmentHomeBinding
import com.zaky.agrocare.ui.cart.CartViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val cartViewModel: CartViewModel by activityViewModels()
    private val favoriteViewModel: com.zaky.agrocare.ui.favorite.FavoriteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val database = AppDatabase.getDatabase(requireContext(), viewLifecycleOwner.lifecycleScope)
        val factory = HomeViewModelFactory(database.productDao())
        val homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        
        setupRecyclerView()
        setupMenuListeners()

        // PERBAIKAN: Menggunakan homeProducts yang dibatasi 4 produk
        homeViewModel.homeProducts.observe(viewLifecycleOwner) { products ->
            val adapter = ProductAdapter(
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
                },
                isFavorite = { product ->
                    favoriteViewModel.isFavorite(product.id)
                },
                onFavoriteClick = { product ->
                    favoriteViewModel.toggleFavorite(product)
                }
            )
            binding.rvProducts.adapter = adapter
            
            favoriteViewModel.favoriteProducts.observe(viewLifecycleOwner) {
                adapter.notifyDataSetChanged()
            }
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
            findNavController().navigate(R.id.navigation_video_education)
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
