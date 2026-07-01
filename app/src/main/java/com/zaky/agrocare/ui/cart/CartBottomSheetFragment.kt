package com.zaky.agrocare.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zaky.agrocare.R
import com.zaky.agrocare.databinding.FragmentCartBottomSheetBinding
import com.zaky.agrocare.utils.toRupiah

class CartBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCartBottomSheetBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CartViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        
        binding.btnCloseCart.setOnClickListener {
            dismiss()
        }
        
        binding.btnCheckout.setOnClickListener {
            if (viewModel.cartItems.value.isNullOrEmpty()) return@setOnClickListener
            findNavController().navigate(R.id.navigation_checkout)
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncrease = { viewModel.increaseQuantity(it) },
            onDecrease = { viewModel.decreaseQuantity(it) },
            onRemove = { viewModel.removeCartItem(it) }
        )
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            // Gunakan submitList untuk ListAdapter
            cartAdapter.submitList(items?.toList())
            
            // Logika Visibilitas
            if (items.isNullOrEmpty()) {
                binding.rvCartItems.visibility = View.GONE
                binding.tvEmptyCart.visibility = View.VISIBLE
                binding.btnCheckout.isEnabled = false
                binding.tvTotalAmount.text = "Rp 0"
            } else {
                binding.rvCartItems.visibility = View.VISIBLE
                binding.tvEmptyCart.visibility = View.GONE
                binding.btnCheckout.isEnabled = true
            }
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { price ->
            binding.tvTotalAmount.text = price.toRupiah()
        }

        // Observe pesan peringatan stok
        viewModel.stockWarningEvent.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
                viewModel.clearStockWarning()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
