package com.example.hair_booking.ui.normal_user.booking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChooseSalonViewModel: ViewModel() {
    private var _salonList = MutableLiveData<ArrayList<Salon>>()
    val salonList: LiveData<ArrayList<Salon>> = _salonList

    init {
        getSalonList()
    }

    private fun getSalonList() {
        // getSalonListForBooking() will return mutable live data
        // => we need to observe it to catch the return when database has finished querying
        dbServices.getSalonServices()?.getSalonListForBooking()
            ?.observeForever{ salonList ->
                _salonList.value = salonList
            }
    }
}