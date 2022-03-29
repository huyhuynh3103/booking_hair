package com.example.hair_booking.ui.normal_user.salon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NormalUserSalonDetailViewModel: ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _avatar = MutableLiveData<String>()
    val avatar: LiveData<String> = _avatar

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _openHour = MutableLiveData<String>()
    val openHour: LiveData<String> = _openHour

    private val _closeHour = MutableLiveData<String>()
    val closeHour: LiveData<String> = _closeHour

    private val _rate = MutableLiveData<String>()
    val rate: LiveData<String> = _rate

    private val _address = MutableLiveData<HashMap<String, String>>()
    val address: LiveData<HashMap<String, String>> = _address
}