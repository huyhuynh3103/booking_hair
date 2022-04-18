package com.example.hair_booking.ui.manager.stylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class ManagerStylistListViewModel: ViewModel() {
    private val _stylistList: MutableLiveData<ArrayList<Stylist>> = MutableLiveData<ArrayList<Stylist>>()
    val stylistList: LiveData<ArrayList<Stylist>> = _stylistList

    init {
        viewModelScope.launch {
            getStylistList()
        }
    }

    private suspend fun getStylistList() {
        _stylistList.value = dbServices.getStylistServices()?.findAll()
    }

    suspend fun getUpdatedStylistList() {
        _stylistList.value = dbServices.getStylistServices()?.findAll()
    }
}