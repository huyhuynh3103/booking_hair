package com.example.hair_booking.ui.normal_user.history.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryBookingDetailViewModelFactory(private val id:MutableLiveData<String>):ViewModelProvider.NewInstanceFactory() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryBookingDetailViewModel(id) as T
    }
}