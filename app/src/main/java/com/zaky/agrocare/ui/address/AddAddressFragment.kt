package com.zaky.agrocare.ui.address

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.data.AddressManager
import com.zaky.agrocare.databinding.FragmentAddAddressBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class AddAddressFragment : Fragment() {

    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!

    private var currentGeoPoint: GeoPoint? = null
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // KONFIGURASI ANTI-BLANK (Sangat Penting): 
        // Dipanggil SEBELUM layout di-inflate
        Configuration.getInstance().userAgentValue = requireContext().packageName
        
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupActions()
    }

    private fun setupMap() {
        val mapView = binding.mapView
        mapView.setMultiTouchControls(true)
        
        // Default zoom level
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        
        // Default point (Jakarta)
        val defaultPoint = GeoPoint(-6.200000, 106.816666)
        mapController.setCenter(defaultPoint)
        updateMarker(defaultPoint)

        // Event listener for tapping the map
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                updateMarker(p)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        
        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(0, mapEventsOverlay)
        
        checkLocationPermission()
    }
    
    private fun updateMarker(point: GeoPoint) {
        val mapView = binding.mapView
        currentGeoPoint = point
        
        if (marker == null) {
            marker = Marker(mapView)
            marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
        }
        marker?.position = point
        mapView.invalidate()
        
        updateAddressFromGeoPoint(point)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Bisa menggunakan FusedLocationProvider dari play-services-location
            // Untuk FusedLocationClient:
            com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(requireActivity())
                .lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val userPoint = GeoPoint(location.latitude, location.longitude)
                        binding.mapView.controller.animateTo(userPoint)
                        updateMarker(userPoint)
                    }
                }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
        }
    }

    private fun updateAddressFromGeoPoint(point: GeoPoint) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                
                withContext(Dispatchers.Main) {
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val addressText = address.getAddressLine(0)
                        binding.tvPinpointAddress.text = addressText
                        
                        if (binding.etFullAddress.text.isNullOrBlank()) {
                            binding.etFullAddress.setText(addressText)
                        }
                    } else {
                        binding.tvPinpointAddress.text = "Alamat tidak ditemukan di titik ini"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvPinpointAddress.text = "Lat: ${point.latitude}, Lng: ${point.longitude}"
                }
            }
        }
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveAddress.setOnClickListener {
            val title = binding.etAddressTitle.text.toString()
            val fullAddress = binding.etFullAddress.text.toString()
            val isDefault = binding.cbSetDefault.isChecked

            if (title.isBlank() || fullAddress.isBlank()) {
                Toast.makeText(requireContext(), "Harap lengkapi judul dan detail alamat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tampilkan latitude longitude dari OSMDroid sesuai instruksi user
            val lat = currentGeoPoint?.latitude
            val lng = currentGeoPoint?.longitude
            Toast.makeText(requireContext(), "Lokasi Dipilih!\nLat: $lat, Lng: $lng", Toast.LENGTH_LONG).show()

            viewLifecycleOwner.lifecycleScope.launch {
                AddressManager.addAddress(
                    title = title,
                    fullAddress = fullAddress,
                    lat = lat,
                    lng = lng,
                    isDefault = isDefault
                )
                Toast.makeText(requireContext(), "Alamat berhasil disimpan", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDetach()
        _binding = null
    }
}
