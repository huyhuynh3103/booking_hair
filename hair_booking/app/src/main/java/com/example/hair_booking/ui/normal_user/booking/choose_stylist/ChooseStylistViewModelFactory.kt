package com.example.hair_booking.ui.normal_user.booking.choose_stylist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChooseStylistViewModelFactory(private val chosenSalonId: MutableLiveData<String>,
                                    private val chosenShiftId: MutableLiveData<String>,
                                    private val chosenTimeRange: MutableLiveData<Pair<Float, Float>>,
                                    private val chosenDate: MutableLiveData<String>,)
    : ViewModelProvider.NewInstanceFactory() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChooseStylistViewModel(chosenSalonId, chosenShiftId, chosenTimeRange, chosenDate) as T
    }
}