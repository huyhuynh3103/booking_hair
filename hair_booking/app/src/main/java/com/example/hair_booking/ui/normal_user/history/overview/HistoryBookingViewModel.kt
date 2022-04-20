package com.example.hair_booking.ui.normal_user.history.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch
class HistoryBookingViewModel: ViewModel() {
    private val _historyList = MutableLiveData<ArrayList<Appointment>>()
    val historyList:LiveData<ArrayList<Appointment>> = _historyList
    init {
        viewModelScope.launch {
            val currentUser = AuthRepository.getCurrentUser()
            try {
               val listAppointment = dbServices.getAppointmentServices()?.getAppointmentListForUser(currentUser!!.email!!)
               _historyList.value = listAppointment!!
            }catch (e:Exception){
                Log.e("huy-exception",e.message.toString(),e)
                throw e
            }
        }
    }

}