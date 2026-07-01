package com.zaky.agrocare.ui.category

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
import androidx.navigation.fragment.navArgs
import com.zaky.agrocare.R
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.data.Product
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.databinding.FragmentProductCategoryBinding
import com.zaky.agrocare.ui.cart.CartViewModel
import com.zaky.agrocare.ui.home.HomeViewModel
import com.zaky.agrocare.ui.home.HomeViewModelFactory
import com.zaky.agrocare.ui.home.ProductAdapter

class ProductCategoryFragment : Fragment() {

    private var _binding: FragmentProductCategoryBinding? = null
    private val binding get() = _binding!!
    
    private val args: ProductCategoryFragmentArgs by navArgs()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val favoriteViewModel: com.zaky.agrocare.ui.favorite.FavoriteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupRecyclerView()
    }

    private fun setupHeader() {
        binding.tvCategoryTitle.text = args.title

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        // Inisialisasi HomeViewModel untuk mendapatkan data lengkap
        val database = AppDatabase.getDatabase(requireContext(), viewLifecycleOwner.lifecycleScope)
        val factory = HomeViewModelFactory(database.productDao())
        val homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        // Tentukan list produk berdasarkan kategori
        if (args.title == "Semua Produk") {
            // Mengambil semua produk dari ViewModel (Hardcode + Database)
            homeViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                updateAdapter(products)
            }
        } else {
            // Filter manual untuk kategori Bibit atau Pupuk (Hardcode)
            val filteredProducts = if (args.title.contains("Benih", ignoreCase = true) || args.title.contains("Bibit", ignoreCase = true)) {
                listOf(
                    Product(2, "Bibit Cabai Rawit Unggul", "Rp 15.000", "https://picsum.photos/seed/chili/400/300"),
                    Product(4, "Bibit Padi Inpari 32", "Rp 85.000", "https://picsum.photos/seed/rice/400/300"),
                    Product(6, "Benih Jagung Hibrida", "Rp 60.000", "https://picsum.photos/seed/corn/400/300")
                )
            } else if (args.title.contains("Pupuk", ignoreCase = true)) {
                listOf(
                    Product(3, "Pupuk Kompos Premium 5kg", "Rp 45.000", "https://picsum.photos/seed/fertilizer/400/300"),
                    Product(5, "Pupuk Kandang Premium", "Rp 12.000", "https://picsum.photos/seed/sprayer/400/300"),
                    Product(7, "Pupuk Organik Cair (POC)", "Rp 40.000", "https://picsum.photos/seed/corn/400/300")
                )
            } else {
                emptyList()
            }
            updateAdapter(filteredProducts)
        }
    }

    private fun updateAdapter(products: List<Product>) {
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
                val stock = com.zaky.agrocare.data.StockManager.getStock(product.id)
                cartViewModel.addToCart(
                    CartItem(
                        id = product.id,
                        name = product.name,
                        price = priceInt,
                        imageUrl = product.imageUrl,
                        quantity = 1,
                        stock = stock
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
