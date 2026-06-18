package com.zaky.agrocare.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _userName = MutableLiveData<String>().apply {
        value = "Pengguna AgroCare"
    }
    val userName: LiveData<String> = _userName
}
