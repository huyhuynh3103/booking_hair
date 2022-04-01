package com.example.hair_booking.ui.manager.stylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices

class ManagerStylistDetailViewModel: ViewModel() {
    private val _stylist: MutableLiveData<Stylist> = MutableLiveData()
    val stylist: LiveData<Stylist> = _stylist

    fun getStylistDetail(id: String){
        dbServices.getStylistServices()?.getStylistDetail(id)?.observeForever {
            _stylist.value = it
        }
    }
}