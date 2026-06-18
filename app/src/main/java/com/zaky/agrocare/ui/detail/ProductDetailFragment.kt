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
