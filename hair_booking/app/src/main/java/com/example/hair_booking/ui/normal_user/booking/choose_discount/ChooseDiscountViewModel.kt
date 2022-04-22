package com.example.hair_booking.ui.normal_user.booking.choose_discount

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Discount
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.services.local.DiscountServices
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class ChooseDiscountViewModel: ViewModel() {
    private var _discountList = MutableLiveData<ArrayList<Discount>>()
    val discountList: LiveData<ArrayList<Discount>> = _discountList

    private var _userId: String = ""
    private var _chosenDate: String = ""
    private var _serviceId: String = ""

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun getDiscountList() {
        _discountList.value = DiscountServices.getUnusedDiscounts(_userId, _chosenDate, _serviceId)
    }

    fun setupDiscountList() {
        viewModelScope.launch {
            getDiscountList()
        }
    }

    fun setUserId(userId: String) {
        this._userId = userId
    }

    fun setServiceId(serviceId: String) {
        this._serviceId = serviceId
    }

    fun setChosenDate(chosenDate: String) {
        this._chosenDate = chosenDate
    }
}