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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAmountOfServicesBooked(): Map<String, Int>? {
        val salonId: String = "b2W2npkzmw6EgHWVovPM"
        return dbServices.getAppointmentServices()?.getAmountOfServicesBooked(salonId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAmountOfShiftsBooked(): Map<String, Int>? {
        val salonId: String = "b2W2npkzmw6EgHWVovPM"
        var result : Map<String, Int>? = emptyMap()
        result = dbServices.getAppointmentServices()?.getAmountOfShiftsBooked(salonId)
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRevenueOfNLastDays(): ArrayList<Pair<String, Long>>? {
        val salonId: String = "b2W2npkzmw6EgHWVovPM"
        var result : ArrayList<Pair<String, Long>>? =
            dbServices.getAppointmentServices()?.getRevenueOfNLastDays(6, salonId)
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRevenueOfNLastMonths(): ArrayList<Pair<Pair<Int, Int>, Long>>? {
        val salonId: String = "b2W2npkzmw6EgHWVovPM"
        var result: ArrayList<Pair<Pair<Int, Int>, Long>>? =
            dbServices.getAppointmentServices()?.getRevenueOfNLastMonths(6, salonId)
        return result
    }

}