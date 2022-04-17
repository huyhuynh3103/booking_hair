package com.example.hair_booking.ui.normal_user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class NormalUserProfileViewModel: ViewModel() {
    private val _normalUser: MutableLiveData<NormalUser> = MutableLiveData()
    val normalUser: LiveData<NormalUser> = _normalUser
    private val _account: MutableLiveData<Account> = MutableLiveData()
    val account: LiveData<Account> = _account

    fun getNormalUserDetail(id: String){
        dbServices.getNormalUserServices()?.getNormalUserDetail(id)?.observeForever {
            _normalUser.value = it
        }
    }

    fun getUserAccountDetail(id: String){
        viewModelScope.launch {
            _account.value = dbServices.getNormalUserServices()?.getNormalUserAccountDetail(id)
        }
    }

    // Set save button onclick to be observable
    private val _saveBtnClicked = MutableLiveData<Boolean>()
    val saveBtnClicked: LiveData<Boolean> = _saveBtnClicked

    fun onLockBtnClicked() {
        _saveBtnClicked.value = true
    }

    suspend fun updateNormalUser(fullname: String, phone: String, gender: String, id: String) {

        dbServices.getNormalUserServices()!!.updateNormalUser(fullname, phone, gender, id)
    }

}