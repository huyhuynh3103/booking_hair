package com.example.hair_booking.ui.normal_user.booking.booking_confirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookingConfirmViewModelFactory(private val appointmentSaved: MutableLiveData<HashMap<String, *>?>)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookingConfirmViewModel(appointmentSaved) as T
    }
}