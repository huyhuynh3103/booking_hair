package com.example.hair_booking.ui.normal_user.booking.choose_stylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices

class ChooseStylistViewModel(private val chosenSalonId: MutableLiveData<String>) : ViewModel() {
    private val _stylistList: MutableLiveData<ArrayList<Stylist>> = MutableLiveData<ArrayList<Stylist>>()
    val stylistList: LiveData<ArrayList<Stylist>> = _stylistList

    init {
        getStylistList()
    }

    private fun getStylistList() {
        // chosenSalonId is assigned value after the view model is created
        // => we need to observe it to know when the value is assigned
        chosenSalonId.observeForever{
            // getStylistListForBooking() will return mutable live data
            // => we need to observe it to catch the return when database has finished querying
            dbServices.getStylistServices()?.getStylistListForBooking(it) // get only stylists of chosen salon
                ?.observeForever{ stylistList ->
                    _stylistList.value = stylistList
                }
        }
    }

}