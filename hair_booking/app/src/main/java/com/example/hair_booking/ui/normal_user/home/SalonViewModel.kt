package com.example.hair_booking.ui.normal_user.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.db.dbServices
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SalonViewModel: ViewModel() {
    private val _hairSalon = MutableLiveData<ArrayList<Salon>>()
    val hairSalon:LiveData<ArrayList<Salon>> = _hairSalon

    init {
        viewModelScope.launch {
            fetchSalon()
        }
    }

    private suspend fun fetchSalon(){
        dbServices.hairSalonServices.findAll().observeForever{
            salons ->
            salons.forEach { salon ->
                Log.d("huy-test-fetch",salon.name.toString())
                Log.d("huy-test-fetch",salon.avatar.toString())
                Log.d("huy-test-fetch",salon.rate.toString())
            }
            _hairSalon.value = salons
        }
    }
}