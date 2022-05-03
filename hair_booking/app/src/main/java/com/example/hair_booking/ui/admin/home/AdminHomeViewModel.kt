package com.example.hair_booking.ui.admin.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class AdminHomeViewModel: ViewModel() {

    private val _salonList: MutableLiveData<ArrayList<Salon>> = MutableLiveData()
    val salonList: LiveData<ArrayList<Salon>> = _salonList

    init {
        viewModelScope.launch {
            getSalonListSpinner()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRevenueOfNLastMonths(salonId: String): ArrayList<Pair<Pair<Int, Int>, Long>>? {
        var result: ArrayList<Pair<Pair<Int, Int>, Long>>? =
            dbServices.getAppointmentServices()?.getRevenueOfNLastMonths(6, salonId)
        return result
    }

    suspend fun getSalonList() : ArrayList<Salon>? {
        var result: ArrayList<Salon>? =
            dbServices.getSalonServices()?.getSalonListForStatistics()
        return result
    }

    private suspend fun getSalonListSpinner() {
        _salonList.value = dbServices.getSalonServices()?.FindAll()
    }

}