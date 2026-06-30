package com.zaky.agrocare.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.zaky.agrocare.R
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.data.Product
import com.zaky.agrocare.databinding.FragmentProductCategoryBinding
import com.zaky.agrocare.ui.cart.CartViewModel
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
        val dummyProducts = if (args.title.contains("Benih", ignoreCase = true)) {
            listOf(
                Product(1, "Benih Tomat Unggul", "Rp 15.000", "https://example.com/tomat.jpg"),
                Product(2, "Bibit Cabai Rawit", "Rp 12.500", "https://example.com/cabai.jpg"),
                Product(3, "Benih Jagung Manis", "Rp 20.000", "https://example.com/jagung.jpg"),
                Product(4, "Bibit Selada Hidroponik", "Rp 10.000", "https://example.com/selada.jpg")
            )
        } else {
            listOf(
                Product(5, "Pupuk Organik Cair", "Rp 45.000", "https://example.com/pupuk1.jpg"),
                Product(6, "Pupuk NPK Mutiara", "Rp 35.000", "https://example.com/pupuk2.jpg"),
                Product(7, "Kompos Kambing Matang", "Rp 15.000", "https://example.com/pupuk3.jpg"),
                Product(8, "Bio-Aktivator EM4", "Rp 25.000", "https://example.com/pupuk4.jpg")
            )
        }

        val adapter = ProductAdapter(
            dummyProducts,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
