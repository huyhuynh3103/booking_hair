package com.example.hair_booking.ui.admin.service.overview

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Service
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AdminServiceListViewModel: ViewModel() {

    private var _serviceList = MutableLiveData<ArrayList<Service>>()
    val serviceList: LiveData<ArrayList<Service>> = _serviceList

    private var _serviceToBeEditedId = MutableLiveData<String>()
    val serviceToBeEditedId: LiveData<String> = _serviceToBeEditedId

    // Set add button onclick to be observable
    private val _addNewServiceBtnClicked = MutableLiveData<Boolean>()
    val addNewServiceBtnClicked: LiveData<Boolean> = _addNewServiceBtnClicked
    fun onAddNewServiceBtnClicked() {
        _addNewServiceBtnClicked.value = true
    }

    // Set edit button onclick to be observable
    private val _editServiceBtnClicked = MutableLiveData<Boolean>()
    val editServiceBtnClicked: LiveData<Boolean> = _editServiceBtnClicked
    fun onEditServiceBtnClicked(serviceId: String) {
        _serviceToBeEditedId.value = serviceId
        _editServiceBtnClicked.value = true
    }

    init {
        viewModelScope.launch {
            getServiceList()

            val test = dbServices.getAppointmentServices()!!.getRevenueOfNLastDays(7)

            val a = 2
        }
    }



    private fun getServiceList() {
        dbServices.getServiceServices()!!.getServiceListForAdmin(_serviceList)!!
    }
}