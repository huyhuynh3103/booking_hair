package com.example.hair_booking.ui.normal_user.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Stylist

class ChooseStylistViewModel : ViewModel() {
    private val _stylistList: MutableLiveData<ArrayList<Stylist>> = MutableLiveData<ArrayList<Stylist>>()
    val stylistList: LiveData<ArrayList<Stylist>> = _stylistList

    init {
        getStylistList()
    }

    private fun getStylistList() {
        _stylistList.value = arrayListOf(
            Stylist(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq",
                "null"
            ),
            Stylist(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq",
                "null"
            ),
            Stylist(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq",
                "null"
            ),
            Stylist(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq",
                "null"
            ),
            Stylist(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq",
                "null"
            ),
            Stylist(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq",
                "null"
            )
        )
    }

}