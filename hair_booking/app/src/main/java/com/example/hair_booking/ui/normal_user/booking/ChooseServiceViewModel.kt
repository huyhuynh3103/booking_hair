package com.example.hair_booking.ui.normal_user.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Service

class ChooseServiceViewModel: ViewModel() {
    private val _serviceList = MutableLiveData<ArrayList<Service>>()
    val serviceList: LiveData<ArrayList<Service>> = _serviceList

    init {
        getServiceList()
    }

    private fun getServiceList() {
        _serviceList.value = arrayListOf(
            Service(
                "testid",
                "testname",
                100000,
                "Tasdq"
            ),
            Service(
                "testid",
                "testname",
                100000,
                "Tasdq"
            ),
            Service(
                "testid",
                "testname",
                100000,
                "Tasdq"
            ),
            Service(
                "testid",
                "testname",
                100000,
                "Tasdq"
            ),
            Service(
                "testid",
                "testname",
                100000,
                "Tasdq"
            ),
            Service(
                "testid",
                "testname",
                100000,
                "Tasdq"
            ),
            Service(
                "testid",
                "testname",
                100000,
                "Tasdq"
            )
        )
    }
}