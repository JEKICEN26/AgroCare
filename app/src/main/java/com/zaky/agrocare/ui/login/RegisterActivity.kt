package com.zaky.agrocare.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.data.local.UserEntity
import com.zaky.agrocare.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password dan Konfirmasi Password tidak cocok!", Toast.LENGTH_SHORT).show()
            return
        }

        val database = AppDatabase.getDatabase(this, lifecycleScope)
        val userDao = database.userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val exists = userDao.checkUsernameExists(username) > 0
            if (exists) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Username sudah terdaftar!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val newUser = UserEntity(
                    username = username,
                    email = email,
                    phone = phone,
                    passwordHash = password // Sebaiknya dihash di app sesungguhnya
                )
                userDao.insertUser(newUser)
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke layar login
                }
            }
        }
    }
}
