package com.example.hair_booking.ui.manager.stylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices

class ManagerStylistListViewModel: ViewModel() {
    private val _stylistList: MutableLiveData<ArrayList<Stylist>> = MutableLiveData<ArrayList<Stylist>>()
    val stylistList: LiveData<ArrayList<Stylist>> = _stylistList

    init {
        getStylistList()
    }

    private fun getStylistList() {
        dbServices.getStylistServices()?.getStylistListForBooking()
            ?.observeForever{ stylistList ->
                _stylistList.value = stylistList
            }
    }

    fun getUpdatedStylistList() {
        dbServices.getStylistServices()?.getStylistListForBooking()
            ?.observeForever{ stylistList ->
                _stylistList.value = stylistList
            }
    }
}