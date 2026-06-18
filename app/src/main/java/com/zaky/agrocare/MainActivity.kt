package com.zaky.agrocare

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.zaky.agrocare.databinding.ActivityMainBinding
import com.zaky.agrocare.ui.cart.CartBottomSheetFragment
import com.zaky.agrocare.ui.cart.CartViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // Inisialisasi ViewModel di level Activity agar bisa diakses semua fragment
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_checkout, R.id.navigation_product_detail -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }

        // Observasi jumlah item untuk update badge di icon keranjang
        cartViewModel.totalItems.observe(this) { count ->
            if (count > 0) {
                binding.tvCartBadge.visibility = View.VISIBLE
                binding.tvCartBadge.text = count.toString()
            } else {
                binding.tvCartBadge.visibility = View.GONE
            }
        }

        setupTopBarActions()
    }

    private fun setupTopBarActions() {
        binding.btnCart.setOnClickListener {
            val cartBottomSheet = CartBottomSheetFragment()
            cartBottomSheet.show(supportFragmentManager, "CartBottomSheet")
        }

        binding.btnChat.setOnClickListener {
            // Aksi buka chat
        }
    }
}
