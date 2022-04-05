package com.example.hair_booking.ui.normal_user.booking.choose_discount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Discount
import com.example.hair_booking.services.db.dbServices

class ChooseDiscountViewModel: ViewModel() {
    private var _discountList = MutableLiveData<ArrayList<Discount>>()
    val discountList: LiveData<ArrayList<Discount>> = _discountList

    init {
        getDiscountList()
    }

    private fun getDiscountList() {
//        // getSalonListForBooking() will return mutable live data
//        // => we need to observe it to catch the return when database has finished querying
//        dbServices.getSalonServices()?.getSalonListForBooking()
//            ?.observeForever{ salonList ->
//                _discountList.value = salonList
//            }
    }
}