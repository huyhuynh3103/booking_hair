package com.example.hair_booking.ui.normal_user.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Salon

class ChooseSalonViewModel: ViewModel() {
    private val _salonList = MutableLiveData<ArrayList<Salon>>()
    val salonList: LiveData<ArrayList<Salon>> = _salonList

    init {
        getSalonList()
    }

    private fun getSalonList() {
        _salonList.value = arrayListOf(
            Salon(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq"
            ),
            Salon(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq"
            ),
            Salon(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq"
            ),
            Salon(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq"
            ),
            Salon(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq"
            ),
            Salon(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq"
            ),
            Salon(
                "testname",
                "avatar",
                "asdklq",
                "Tasdq"
            )
        )
    }
}