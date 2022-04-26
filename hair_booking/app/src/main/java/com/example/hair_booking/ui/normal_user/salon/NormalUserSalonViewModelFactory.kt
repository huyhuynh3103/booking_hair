package com.example.hair_booking.ui.normal_user.salon

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NormalUserSalonViewModelFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NormalUserSalonDetailViewModel(context) as T
    }
}