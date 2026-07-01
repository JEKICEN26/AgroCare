package com.zaky.agrocare.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zaky.agrocare.databinding.FragmentSettingsProfileBinding
import com.zaky.agrocare.utils.SessionManager

class SettingsProfileFragment : Fragment() {

    private var _binding: FragmentSettingsProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

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

    private val pickImageLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            sessionManager.saveProfileImage(uri.toString())
            loadProfileData()
        }
    }

    private fun loadProfileData() {
        binding.etName.setText(sessionManager.getName())
        binding.etEmail.setText(sessionManager.getEmail())
        binding.etPhone.setText(sessionManager.getPhone())

        val imageUriString = sessionManager.getProfileImage()
        if (imageUriString != null) {
            val uri = android.net.Uri.parse(imageUriString)
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

            sessionManager.saveProfileData(name, email, phone)
            Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.btnChangeImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.menuChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
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
