package com.example.hair_booking.ui.manager.appointment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.db.dbServices

class AppointmentListViewModel: ViewModel() {
    private var _appointmentList = MutableLiveData<ArrayList<Appointment>>()
    val appointmentList: LiveData<ArrayList<Appointment>> = _appointmentList

    init {
        getAppointmentList()
    }

    private fun getAppointmentList() {
        // getAppointmentListForManager() will return mutable live data
        // => we need to observe it to catch the return when database has finished querying
        dbServices.getAppointmentServices()?.getAppointmentListForManager()
            ?.observeForever{ appointmentList ->
                _appointmentList.value = appointmentList
            }
    }
}