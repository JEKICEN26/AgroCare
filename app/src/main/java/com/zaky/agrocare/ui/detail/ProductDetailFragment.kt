package com.zaky.agrocare.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.zaky.agrocare.R
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.databinding.FragmentProductDetailBinding
import com.zaky.agrocare.ui.cart.CartViewModel

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    
    private val cartViewModel: CartViewModel by activityViewModels()
    private val favoriteViewModel: com.zaky.agrocare.ui.favorite.FavoriteViewModel by activityViewModels()
    
    // Mengambil data produk dari navigasi
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        displayProductDetail()
        setupActions()
    }

    private fun displayProductDetail() {
        binding.apply {
            tvProductName.text = args.productName
            tvProductPrice.text = args.productPrice
            ivProductDetail.load(args.productImage) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val currentProduct = com.zaky.agrocare.data.Product(
            id = args.productId,
            name = args.productName,
            price = args.productPrice,
            imageUrl = args.productImage
        )

        favoriteViewModel.favoriteProducts.observe(viewLifecycleOwner) {
            val isFav = favoriteViewModel.isFavorite(args.productId)
            if (isFav) {
                binding.btnFavoriteDetail.setImageResource(com.zaky.agrocare.R.drawable.ic_heart_filled)
                binding.btnFavoriteDetail.setColorFilter(android.graphics.Color.parseColor("#F44336"))
            } else {
                binding.btnFavoriteDetail.setImageResource(com.zaky.agrocare.R.drawable.ic_heart_outline)
                binding.btnFavoriteDetail.setColorFilter(android.graphics.Color.parseColor("#757575"))
            }
        }

        binding.btnFavoriteDetail.setOnClickListener {
            val isFav = favoriteViewModel.isFavorite(args.productId)
            favoriteViewModel.toggleFavorite(currentProduct)
            if (!isFav) {
                android.widget.Toast.makeText(requireContext(), "Berhasil ditambahkan ke Favorit Saya", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(requireContext(), "Berhasil dihapus dari Favorit Saya", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnChatMitra.setOnClickListener {
            val mitraName = binding.tvMitraName.text.toString()
            val action = ProductDetailFragmentDirections.actionDetailToChat(mitraName)
            findNavController().navigate(action)
        }

        binding.btnAddToCart.setOnClickListener {
            // Konversi harga String (Rp 12.500) ke Int (12500)
            val cleanPrice = args.productPrice
                .replace("Rp", "")
                .replace(".", "")
                .replace(" ", "")
                .trim()
                .toIntOrNull() ?: 0

            val item = CartItem(
                id = args.productId,
                name = args.productName,
                price = cleanPrice,
                quantity = 1,
                imageUrl = args.productImage
            )
            
            cartViewModel.addToCart(item)
            
            Snackbar.make(binding.root, "${args.productName} berhasil ditambahkan!", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(android.R.color.holo_green_dark, null))
                .setAnchorView(binding.btnAddToCart)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
