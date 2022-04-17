package com.example.hair_booking.ui.manager.appointment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class AppointmentListViewModel: ViewModel() {
    private var _appointmentList = MutableLiveData<ArrayList<Appointment>>()
    val appointmentList: LiveData<ArrayList<Appointment>> = _appointmentList

    init {
        viewModelScope.launch {
            getAppointmentList()
        }
    }

    private suspend fun getAppointmentList() {
//        dbServices.getAppointmentServices()?.getAppointmentListForManager()
//            ?.observeForever{ appointmentList ->
//                _appointmentList.v = appointmentList
//            }
        dbServices.getAppointmentServices()?.getAppointmentListForManager(_appointmentList)!!
    }
}