package com.zaky.agrocare.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zaky.agrocare.R
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.databinding.FragmentFavoriteBinding
import com.zaky.agrocare.ui.cart.CartViewModel
import com.zaky.agrocare.ui.home.ProductAdapter

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)

        favoriteViewModel.favoriteProducts.observe(viewLifecycleOwner) { favorites ->
            if (favorites.isEmpty()) {
                binding.rvFavorites.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                binding.rvFavorites.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE

                val adapter = ProductAdapter(
                    listProduct = favorites.toList(),
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
                binding.rvFavorites.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
