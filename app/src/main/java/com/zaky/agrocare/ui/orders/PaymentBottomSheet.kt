package com.zaky.agrocare.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zaky.agrocare.databinding.FragmentPaymentBottomSheetBinding

class PaymentBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentPaymentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var onPaymentSuccessListener: ((String) -> Unit)? = null

    fun setOnPaymentSuccessListener(listener: (String) -> Unit) {
        onPaymentSuccessListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbVirtualAccount.id -> {
                    binding.llVirtualAccount.visibility = View.VISIBLE
                    binding.llCreditCard.visibility = View.GONE
                    // Generate random VA number (mock)
                    val randomVa = "880" + (1000000000..9999999999).random().toString()
                    binding.tvVaNumber.text = randomVa
                }
                binding.rbCreditCard.id -> {
                    binding.llVirtualAccount.visibility = View.GONE
                    binding.llCreditCard.visibility = View.VISIBLE
                }
            }
        }

        binding.btnCheckVaPayment.setOnClickListener {
            processPayment("Virtual Account")
        }

        binding.btnCheckCcPayment.setOnClickListener {
            val ccNumber = binding.etCardNumber.text.toString()
            val cvc = binding.etCardCvc.text.toString()

            if (ccNumber.length < 16) {
                binding.etCardNumber.error = "Nomor kartu harus 16 digit"
                return@setOnClickListener
            }
            if (cvc.length < 3) {
                binding.etCardCvc.error = "CVC harus 3 digit"
                return@setOnClickListener
            }
            
            processPayment("Kartu Kredit")
        }
    }

    private fun processPayment(method: String) {
        // Simulasi proses delay bisa ditambahkan di sini, tapi karena ini dummy langsung sukses
        onPaymentSuccessListener?.invoke(method)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
