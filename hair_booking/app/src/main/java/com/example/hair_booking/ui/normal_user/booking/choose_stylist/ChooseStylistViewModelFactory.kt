package com.example.hair_booking.ui.normal_user.booking.choose_stylist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChooseStylistViewModelFactory(private val chosenSalonId: MutableLiveData<String>)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChooseStylistViewModel(chosenSalonId) as T
    }
}