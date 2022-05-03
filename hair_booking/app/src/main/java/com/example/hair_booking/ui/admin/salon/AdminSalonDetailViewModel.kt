package com.example.hair_booking.ui.admin.salon

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices

class AdminSalonDetailViewModel: ViewModel() {
    private val _salon: MutableLiveData<Salon> = MutableLiveData()
    private val _rate = MutableLiveData<Float>()
    val salon: LiveData<Salon> = _salon
    val rate: LiveData<Float> = _rate

    suspend fun getSalonDetail(id: String) {
        _salon.value = dbServices.getSalonServices()?.getSalonById(id)
        _rate.value = salon.value?.rate!!.toFloat()

//        dbServices.getSalonServices()?.getSalonDetail(id)?.observeForever {
//            _salon.value = it
//            _rate.value = it.rate!!.toFloat()
//        }
    }

    suspend fun updateSalon(id: String, salon: Salon) {
        dbServices.getSalonServices()?.updateOne(id, salon)
    }

    suspend fun addSalon(salon: Salon) {
        dbServices.getSalonServices()?.add(salon)
    }

    suspend fun deleteSalon(id: String) {
        dbServices.getSalonServices()?.delete(id)
    }
}