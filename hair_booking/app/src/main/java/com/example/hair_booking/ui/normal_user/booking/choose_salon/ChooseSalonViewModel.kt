package com.example.hair_booking.ui.normal_user.booking.choose_salon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.db.dbServices

class ChooseSalonViewModel: ViewModel() {
    private var _salonList = MutableLiveData<ArrayList<Salon>>()
    val salonList: LiveData<ArrayList<Salon>> = _salonList

//    private var _salonCityList = MutableLiveData<ArrayList<String>>()
//    val salonCityList: LiveData<ArrayList<String>> = _salonCityList
//
//    private var _salonDistrictList = MutableLiveData<ArrayList<String>>()
//    val salonDistrictList: LiveData<ArrayList<String>> = _salonDistrictList

    init {
        getSalonList()
    }

    private fun getSalonList() {
        // getSalonListForBooking() will return mutable live data
        // => we need to observe it to catch the return when database has finished querying
        dbServices.getSalonServices()?.getSalonListForBooking()
            ?.observeForever{ salonList ->
                _salonList.value = salonList

//                var tmpCityList: ArrayList<String> = ArrayList()
//                var tmpDistrictList: ArrayList<String> = ArrayList()
//                salonList.forEach {
//                    tmpCityList.add(it.address?.get("city")!!)
//                    tmpDistrictList.add(it.address?.get("district")!!)
//                }
//
//                _salonCityList.value = tmpCityList
//                _salonDistrictList.value = tmpDistrictList
            }
    }
}