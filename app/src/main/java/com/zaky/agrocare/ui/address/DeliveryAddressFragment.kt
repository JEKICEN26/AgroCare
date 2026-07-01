package com.zaky.agrocare.ui.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaky.agrocare.R
import com.zaky.agrocare.data.AddressManager
import com.zaky.agrocare.databinding.FragmentDeliveryAddressBinding
import kotlinx.coroutines.launch

class DeliveryAddressFragment : Fragment() {

    private var _binding: FragmentDeliveryAddressBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var addressAdapter: AddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupActions()
        observeAddresses()
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            addresses = emptyList(),
            onSetDefaultClick = { address ->
                viewLifecycleOwner.lifecycleScope.launch {
                    AddressManager.setAsDefault(address)
                    Toast.makeText(requireContext(), "Alamat utama diperbarui", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { address ->
                viewLifecycleOwner.lifecycleScope.launch {
                    AddressManager.deleteAddress(address)
                    Toast.makeText(requireContext(), "Alamat dihapus", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvAddresses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
    }

    private fun observeAddresses() {
        viewLifecycleOwner.lifecycleScope.launch {
            AddressManager.getAllAddresses().collect { addresses ->
                addressAdapter.updateData(addresses)
                
                if (addresses.isEmpty()) {
                    binding.rvAddresses.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                } else {
                    binding.rvAddresses.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                }
            }
        }
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_deliveryAddressFragment_to_addAddressFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
