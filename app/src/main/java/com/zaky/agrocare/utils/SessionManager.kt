package com.zaky.agrocare.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AgroCareSession", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_PHONE = "user_phone"
        private const val KEY_PASSWORD = "user_password"
        private const val KEY_PROFILE_IMAGE = "user_profile_image"
        private const val KEY_LOGIN_TYPE = "login_type" // "local" atau "google"
        private const val KEY_GOOGLE_UID = "google_uid"
    }

    fun setLogin(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoginType(type: String) {
        prefs.edit().putString(KEY_LOGIN_TYPE, type).apply()
    }

    fun getLoginType(): String {
        return prefs.getString(KEY_LOGIN_TYPE, "local") ?: "local"
    }

    fun setGoogleUid(uid: String) {
        prefs.edit().putString(KEY_GOOGLE_UID, uid).apply()
    }

    fun getGoogleUid(): String? {
        return prefs.getString(KEY_GOOGLE_UID, null)
    }

    fun saveProfileData(name: String, email: String, phone: String) {
        prefs.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            apply()
        }
    }

    fun saveProfileImage(uri: String) {
        prefs.edit().putString(KEY_PROFILE_IMAGE, uri).apply()
    }

    fun savePassword(password: String) {
        prefs.edit().putString(KEY_PASSWORD, password).apply()
    }

    fun getName(): String {
        return prefs.getString(KEY_NAME, "Alphard Bintang") ?: "Alphard Bintang"
    }

    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, "alphard.bintang@example.com") ?: "alphard.bintang@example.com"
    }

    fun getPhone(): String {
        return prefs.getString(KEY_PHONE, "081234567890") ?: "081234567890"
    }

    fun getProfileImage(): String? {
        return prefs.getString(KEY_PROFILE_IMAGE, null)
    }

    fun getPassword(): String {
        return prefs.getString(KEY_PASSWORD, "alphard123") ?: "alphard123"
    }

    fun logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }
}
