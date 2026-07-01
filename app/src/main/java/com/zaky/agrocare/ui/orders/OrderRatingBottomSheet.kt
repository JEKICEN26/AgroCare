package com.zaky.agrocare.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zaky.agrocare.R
import com.zaky.agrocare.data.OrderManager
import com.zaky.agrocare.databinding.FragmentOrderRatingBinding

class OrderRatingBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentOrderRatingBinding? = null
    private val binding get() = _binding!!
    
    private var currentRating = 0
    private var orderId: String? = null
    private var onRatingSubmitted: (() -> Unit)? = null

    companion object {
        private const val ARG_ORDER_ID = "order_id"
        
        fun newInstance(orderId: String): OrderRatingBottomSheet {
            return OrderRatingBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_ORDER_ID, orderId)
                }
            }
        }
    }
    
    fun setOnRatingSubmittedListener(listener: () -> Unit) {
        onRatingSubmitted = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderRatingBinding.inflate(inflater, container, false)
        orderId = arguments?.getString(ARG_ORDER_ID)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
        
        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                setRating(index + 1, stars)
            }
        }
        
        binding.btnSubmitReview.setOnClickListener {
            if (currentRating == 0) {
                Toast.makeText(requireContext(), "Silakan pilih rating bintang terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Tandai pesanan sebagai sudah di-rating
            orderId?.let { id ->
                OrderManager.rateOrder(id)
            }
            
            Toast.makeText(requireContext(), "Penilaian $currentRating bintang berhasil dikirim! Terima kasih.", Toast.LENGTH_SHORT).show()
            onRatingSubmitted?.invoke()
            dismiss()
        }
    }
    
    private fun setRating(rating: Int, stars: List<ImageView>) {
        currentRating = rating
        val activeColor = ContextCompat.getColor(requireContext(), R.color.icon_warm_orange)
        val inactiveColor = ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
        
        for (i in stars.indices) {
            if (i < rating) {
                stars[i].setColorFilter(activeColor)
            } else {
                stars[i].setColorFilter(inactiveColor)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
