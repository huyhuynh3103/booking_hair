package com.example.hair_booking.ui.manager.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ManagerHomeViewModel: ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAmountOfServicesBooked(salonId: String): Map<String, Int>? {
        return dbServices.getAppointmentServices()?.getAmountOfServicesBooked(salonId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAmountOfShiftsBooked(salonId: String): Map<String, Int>? {
        var result: Map<String, Int>? = emptyMap()
        result = dbServices.getAppointmentServices()?.getAmountOfShiftsBooked(salonId)
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRevenueOfNLastDays(salonId: String): ArrayList<Pair<String, Long>>? {
        var result : ArrayList<Pair<String, Long>>? =
            dbServices.getAppointmentServices()?.getRevenueOfNLastDays(5, salonId)
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRevenueOfNLastMonths(salonId: String): ArrayList<Pair<Pair<Int, Int>, Long>>? {
        var result: ArrayList<Pair<Pair<Int, Int>, Long>>? =
            dbServices.getAppointmentServices()?.getRevenueOfNLastMonths(6, salonId)
        return result
    }

    suspend fun getCurrentUserInfo() : String {
        val currentUserEmail: String? = AuthRepository.getCurrentUser()!!.email
        if(currentUserEmail != null) {
            val currentUser = dbServices.getAccountServices()!!.getManagerAccountByEmail(currentUserEmail)
            if(currentUser != null) {
                return currentUser.hairSalon!!.id
            }
        }
        return ""
    }

}