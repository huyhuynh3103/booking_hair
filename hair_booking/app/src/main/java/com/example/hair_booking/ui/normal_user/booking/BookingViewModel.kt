package com.example.hair_booking.ui.normal_user.booking

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Stylist

class BookingViewModel: ViewModel() {
    private val _salonLocation = MutableLiveData<String>()
    val salonLocation: LiveData<String> = _salonLocation

    private val _services = MutableLiveData<String>()
    val services: LiveData<String> = _services

    private val _stylist = MutableLiveData<String>()
    val stylist: LiveData<String> = _stylist

    private val _bookingDate = MutableLiveData<String>()
    val bookingDate: LiveData<String> = _bookingDate

    private val _bookingTime = MutableLiveData<String>()
    val bookingTime: LiveData<String> = _bookingTime

    private val _note = MutableLiveData<String>()
    val note: LiveData<String> = _note


}