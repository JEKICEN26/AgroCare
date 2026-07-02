package com.zaky.agrocare.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.databinding.FragmentSettingsProfileBinding
import com.zaky.agrocare.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsProfileFragment : Fragment() {

    private var _binding: FragmentSettingsProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var currentPhotoUri: Uri? = null

    // Launcher untuk Galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            saveImageUriAndLoad(uri)
        }
    }

    // Launcher untuk Kamera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && currentPhotoUri != null) {
            saveImageUriAndLoad(currentPhotoUri!!)
        }
    }

    // Launcher untuk Request Permission Kamera
    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsProfileBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        loadProfileData()
        setupListeners()

        return binding.root
    }

    private fun saveImageUriAndLoad(uri: Uri) {
        sessionManager.saveProfileImage(uri.toString())
        
        // Save to room immediately if local
        if (sessionManager.getLoginType() == "local") {
            val oldName = sessionManager.getName()
            val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
            val userDao = database.userDao()

            lifecycleScope.launch(Dispatchers.IO) {
                val user = userDao.getUserByUsername(oldName)
                if (user != null) {
                    val updatedUser = user.copy(profileImage = uri.toString())
                    userDao.updateUser(updatedUser)
                }
            }
        }
        
        loadProfileData()
    }

    private fun loadProfileData() {
        binding.etName.setText(sessionManager.getName())
        binding.etEmail.setText(sessionManager.getEmail())
        binding.etPhone.setText(sessionManager.getPhone())

        val imageUriString = sessionManager.getProfileImage()
        if (imageUriString != null) {
            val uri = Uri.parse(imageUriString)
            binding.ivProfileImage.setImageURI(uri)
            binding.ivProfileImage.visibility = View.VISIBLE
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val oldName = sessionManager.getName()

            // Update SessionManager
            sessionManager.saveProfileData(name, email, phone)

            // Sinkronisasi ke Room jika tipe login adalah lokal
            if (sessionManager.getLoginType() == "local") {
                val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
                val userDao = database.userDao()

                lifecycleScope.launch(Dispatchers.IO) {
                    val user = userDao.getUserByUsername(oldName) // Cari berdasarkan username lama yang tersimpan
                    if (user != null) {
                        val updatedUser = user.copy(username = name, email = email, phone = phone)
                        userDao.updateUser(updatedUser)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Profil dan database berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Profil berhasil diperbarui (Google Account)", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        binding.btnChangeImage.setOnClickListener {
            showImageSourceDialog()
        }

        binding.menuChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Ambil Foto dari Kamera", "Pilih dari Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> pickImageLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(requireContext(), "Gagal membuat file foto", Toast.LENGTH_SHORT).show()
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                it
            )
            currentPhotoUri = photoURI
            takePictureLauncher.launch(photoURI)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun showChangePasswordDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etNewPassword = EditText(context).apply {
            hint = "Kata Sandi Baru"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        layout.addView(etNewPassword)

        AlertDialog.Builder(context)
            .setTitle("Ubah Kata Sandi")
            .setView(layout)
            .setPositiveButton("Simpan") { dialog, _ ->
                val newPassword = etNewPassword.text.toString().trim()
                if (newPassword.isNotEmpty()) {
                    sessionManager.savePassword(newPassword)
                    
                    // Update ke Room juga jika user lokal
                    if (sessionManager.getLoginType() == "local") {
                        val name = sessionManager.getName()
                        val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
                        val userDao = database.userDao()
                        lifecycleScope.launch(Dispatchers.IO) {
                            val user = userDao.getUserByUsername(name)
                            if (user != null) {
                                val updatedUser = user.copy(passwordHash = newPassword)
                                userDao.updateUser(updatedUser)
                            }
                        }
                    }
                    
                    Toast.makeText(context, "Kata sandi berhasil diubah", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
