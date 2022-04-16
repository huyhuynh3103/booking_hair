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

//    init {
//
//        viewModelScope.launch {
//
//            while(true) {
//                Log.d("disvm", "userid: $_userId and date: $_chosenDate")
//                if(_userId.isNotEmpty() && _chosenDate.isNotEmpty()) {
//                    Log.d("disvm", "in while userid: $_userId and date: $_chosenDate")
//                    getDiscountList()
//                    break
//                }
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun getDiscountList() {
        Log.d("disvm", "userid: $_userId and date: $_chosenDate")
        _discountList.value = DiscountServices.getUnusedDiscounts(_userId, _chosenDate)
    }

    fun setupDiscountList(userId: String, chosenDate: String) {
        viewModelScope.launch {
            getDiscountList()
        }
    }

    fun setUserId(userId: String) {
        this._userId = userId
    }

    fun setChosenDate(chosenDate: String) {
        this._chosenDate = chosenDate
    }
}