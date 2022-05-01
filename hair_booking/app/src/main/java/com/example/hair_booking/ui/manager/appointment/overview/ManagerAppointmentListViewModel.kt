package com.example.hair_booking.ui.manager.appointment.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ManagerAppointmentListViewModel: ViewModel() {
    private var _salonId: MutableLiveData<String> = MutableLiveData<String>() // Used to query database
    val salonId: LiveData<String> get() = _salonId // Getter

    private var _appointmentList = MutableLiveData<ArrayList<Appointment>>()
    val appointmentList: LiveData<ArrayList<Appointment>> = _appointmentList

    private var _appointmentSubIds = MutableLiveData<ArrayList<String>>()
    val appointmentSubIds: LiveData<ArrayList<String>> = _appointmentSubIds

    private var _appointmentStylistsName = MutableLiveData<ArrayList<String>>()
    val appointmentStylistsName: LiveData<ArrayList<String>> = _appointmentStylistsName

    init {
        viewModelScope.launch {
            async {
                getCurrentSalonInfo()
            }.await()
            getAppointmentList()
        }
    }


    private suspend fun getCurrentSalonInfo() {
        val currentManagerEmail: String? = AuthRepository.getCurrentUser()!!.email
        if(currentManagerEmail != null) {
            _salonId.value = dbServices.getAccountServices()!!.getManagerAccountByEmail(currentManagerEmail)?.hairSalon?.id
        }
    }

    private fun getAppointmentList() {
        dbServices.getAppointmentServices()?.getAppointmentListForManager(
            salonId.value!!,
            _appointmentList,
            _appointmentSubIds,
            _appointmentStylistsName)!!
    }
}