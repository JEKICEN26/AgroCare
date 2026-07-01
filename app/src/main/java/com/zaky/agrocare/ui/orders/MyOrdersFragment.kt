package com.zaky.agrocare.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.zaky.agrocare.R
import com.zaky.agrocare.data.CartItem
import com.zaky.agrocare.data.Order
import com.zaky.agrocare.data.OrderManager
import com.zaky.agrocare.databinding.FragmentMyOrdersBinding
import com.zaky.agrocare.ui.cart.CartViewModel

class MyOrdersFragment : Fragment() {

    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderAdapter: OrderAdapter
    private val cartViewModel: CartViewModel by activityViewModels()
    private var currentTabIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupHeader()
        setupTabs()
        
        // Baca argumen initialTab dari navigasi (jika ada)
        val initialTab = arguments?.getInt("initialTab", 0) ?: 0
        currentTabIndex = initialTab
        binding.tabLayout.getTabAt(initialTab)?.select()
        
        // Observe perubahan data dari OrderManager
        OrderManager.orders.observe(viewLifecycleOwner) {
            filterOrders(currentTabIndex)
        }
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            emptyList(),
            onItemClick = { order ->
                val detailSheet = OrderDetailBottomSheet(order) { cancelledOrder ->
                    OrderManager.cancelOrder(cancelledOrder.id)
                    Toast.makeText(requireContext(), "Pesanan ${cancelledOrder.productName} berhasil dibatalkan", Toast.LENGTH_SHORT).show()
                }
                detailSheet.show(parentFragmentManager, "OrderDetailSheet")
            },
            onActionClick = { order ->
                handleOrderAction(order)
            }
        )
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }
    
    private fun handleOrderAction(order: Order) {
        when (order.statusId) {
            1 -> {
                // Belum Bayar -> Tampilkan BottomSheet Pembayaran
                val paymentSheet = PaymentBottomSheet()
                paymentSheet.setOnPaymentSuccessListener { method ->
                    // Pindah status ke Dikemas setelah "Cek Pembayaran" di-klik
                    OrderManager.payOrder(order.id, method)
                    Toast.makeText(requireContext(), "Pembayaran berhasil! Pesanan sedang dikemas.", Toast.LENGTH_SHORT).show()
                    
                    // Tambahkan notifikasi
                    com.zaky.agrocare.ui.notifications.NotificationManager.addNotification(
                        requireContext(),
                        com.zaky.agrocare.ui.notifications.NotificationItem(
                            title = "Pembayaran Berhasil!",
                            description = "Pembayaran untuk ${order.productName} telah dikonfirmasi. Pesanan sedang dikemas oleh penjual.",
                            timestamp = "Baru saja"
                        )
                    )
                }
                paymentSheet.show(parentFragmentManager, "PaymentSheet")
            }
            2 -> {
                // Dikemas -> Hubungi Penjual (Chat)
                val bundle = Bundle().apply {
                    putString("mitraName", order.storeName)
                }
                findNavController().navigate(R.id.navigation_chat, bundle)
            }
            3 -> {
                // Dikirim -> Lacak Pesanan
                val trackingSheet = OrderTrackingBottomSheet()
                trackingSheet.show(parentFragmentManager, "OrderTracking")
            }
            4 -> {
                // Selesai
                if (order.isRated) {
                    // Sudah di-rating -> Beli Lagi (tambah ke keranjang)
                    buyAgain(order)
                } else {
                    // Belum di-rating -> Beri Penilaian
                    val ratingSheet = OrderRatingBottomSheet.newInstance(order.id)
                    ratingSheet.setOnRatingSubmittedListener {
                        // Refresh tampilan setelah rating berhasil
                        filterOrders(currentTabIndex)
                    }
                    ratingSheet.show(parentFragmentManager, "OrderRating")
                }
            }
            5 -> {
                // Dibatalkan -> Beli Lagi
                buyAgain(order)
            }
        }
    }
    
    private fun buyAgain(order: Order) {
        val stock = com.zaky.agrocare.data.StockManager.getStock(order.productId)
        val cartItem = CartItem(
            id = order.productId,
            name = order.productName,
            price = order.price,
            quantity = 1,
            imageUrl = order.imageUrl,
            stock = stock
        )
        cartViewModel.addToCart(cartItem)
        Toast.makeText(requireContext(), "${order.productName} ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
    }

    private fun setupTabs() {
        val tabs = listOf("Semua", "Belum Bayar", "Dikemas", "Dikirim", "Selesai", "Dibatalkan")
        for (tabTitle in tabs) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(tabTitle))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { 
                    currentTabIndex = it.position
                    filterOrders(it.position)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun filterOrders(statusIndex: Int) {
        val filteredList = OrderManager.getFilteredOrders(statusIndex)
        
        orderAdapter.updateData(filteredList)
        
        if (filteredList.isEmpty()) {
            binding.rvOrders.visibility = View.GONE
            binding.llEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvOrders.visibility = View.VISIBLE
            binding.llEmptyState.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
