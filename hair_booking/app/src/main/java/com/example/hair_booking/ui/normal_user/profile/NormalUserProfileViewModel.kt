package com.example.hair_booking.ui.normal_user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.services.db.dbServices

class NormalUserProfileViewModel: ViewModel() {
    private val _normalUser: MutableLiveData<NormalUser> = MutableLiveData()
    val normalUser: LiveData<NormalUser> = _normalUser

    fun getSalonDetail(id: String){
        dbServices.getNormalUserServices()?.findById(id)?.observeForever {
            _normalUser.value = it
        }
    }
}