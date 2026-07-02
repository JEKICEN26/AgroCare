package com.zaky.agrocare.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.zaky.agrocare.MainActivity
import com.zaky.agrocare.R
import com.zaky.agrocare.data.local.AppDatabase
import com.zaky.agrocare.databinding.ActivityLoginBinding
import com.zaky.agrocare.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        firebaseAuth = FirebaseAuth.getInstance()
        
        // Cek jika sudah login
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Menjalankan animasi intro
        animateUI()

        // Login lokal (database lokal via Room)
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val database = AppDatabase.getDatabase(this, lifecycleScope)
            val userDao = database.userDao()

            lifecycleScope.launch(Dispatchers.IO) {
                val user = userDao.getUserByUsername(username)
                withContext(Dispatchers.Main) {
                    if (user != null && user.passwordHash == password) {
                        // Simpan data login ke session
                        sessionManager.setLogin(true)
                        sessionManager.setLoginType("local")
                        sessionManager.saveProfileData(
                            name = user.username, // atau tambahkan field nama lengkap di UserEntity nanti
                            email = user.email,
                            phone = user.phone
                        )
                        Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    } else {
                        Toast.makeText(this@LoginActivity, "Username atau password salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Navigasi ke halaman Register
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Login via Google (Firebase Authentication)
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleGoogleSignIn(result)
            } catch (e: Exception) {
                Log.e("LoginActivity", "Google Sign-In gagal: ${e.message}", e)
                Toast.makeText(
                    this@LoginActivity,
                    "Google Sign-In gagal: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleGoogleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            // Autentikasi dengan Firebase
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            // Simpan data Google ke SessionManager
                            sessionManager.setLogin(true)
                            sessionManager.setLoginType("google")
                            sessionManager.setGoogleUid(user.uid)
                            sessionManager.saveProfileData(
                                name = user.displayName ?: "Pengguna Google",
                                email = user.email ?: "",
                                phone = user.phoneNumber ?: ""
                            )
                            // Simpan foto profil Google jika tersedia
                            user.photoUrl?.let {
                                sessionManager.saveProfileImage(it.toString())
                            }

                            Toast.makeText(this, "Login Google Berhasil", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                    } else {
                        Log.e("LoginActivity", "Firebase Auth gagal", task.exception)
                        Toast.makeText(this, "Autentikasi Firebase gagal", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Log.e("LoginActivity", "Tipe credential tidak dikenal")
            Toast.makeText(this, "Tipe credential tidak dikenal", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun animateUI() {
        // Animasi fade in & slide up untuk Logo
        binding.llLogoContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .setStartDelay(200)
            .start()
            
        // Animasi fade in & slide up untuk Card Form Login
        binding.cvLoginCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .setStartDelay(400)
            .start()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
