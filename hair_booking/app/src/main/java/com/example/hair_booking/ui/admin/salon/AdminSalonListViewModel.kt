package com.example.hair_booking.ui.admin.salon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference

class AdminSalonListViewModel: ViewModel() {
    private val _salonList: MutableLiveData<ArrayList<Salon>> = MutableLiveData<ArrayList<Salon>>()
    val salonList: LiveData<ArrayList<Salon>> = _salonList

    suspend fun getSalonList() {
        _salonList.value = dbServices.getSalonServices()?.FindAll()
    }
}