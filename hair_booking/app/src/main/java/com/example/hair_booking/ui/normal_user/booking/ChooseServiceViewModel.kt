package com.example.hair_booking.ui.normal_user.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Service
import com.example.hair_booking.services.db.dbServices

class ChooseServiceViewModel: ViewModel() {
    private val _serviceList = MutableLiveData<ArrayList<Service>>()
    val serviceList: LiveData<ArrayList<Service>> = _serviceList

    init {
        getServiceList()
    }

    private fun getServiceList() {
        // getServiceListForBooking() will return mutable live data
        // => we need to observe it to catch the return when database has finished querying
        dbServices.getServiceServices()?.getServiceListForBooking()
            ?.observeForever{ serviceList ->
                _serviceList.value = serviceList
            }
    }
}