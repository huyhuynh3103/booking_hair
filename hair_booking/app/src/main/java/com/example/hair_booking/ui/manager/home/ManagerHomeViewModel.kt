package com.example.hair_booking.ui.manager.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ManagerHomeViewModel: ViewModel() {

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun getAmountOfServicesBooked(): Map<String, Int>? {
        val salonId: String = "b2W2npkzmw6EgHWVovPM"
        return dbServices.getAppointmentServices()?.getAmountOfServicesBooked(salonId)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun getAmountOfShiftsBooked(): Map<String, Int>? {
        val salonId: String = "b2W2npkzmw6EgHWVovPM"
        var result : Map<String, Int>? = emptyMap()
        result = dbServices.getAppointmentServices()?.getAmountOfShiftsBooked(salonId)
        return result
    }

}