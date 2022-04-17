package com.example.hair_booking.ui.admin.users_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class UserDetailAdminViewModel: ViewModel() {
    private val _user: MutableLiveData<NormalUser> = MutableLiveData()
    val user: LiveData<NormalUser> = _user
    private val _account: MutableLiveData<Account> = MutableLiveData()
    val account: LiveData<Account> = _account

    fun getUserDetail(id: String){
        dbServices.getNormalUserServices()?.getNormalUserDetail(id)?.observeForever {
            _user.value = it
        }
        //dbServices.getNormalUserServices()?.getNormalUserAccountDetail(id)?.observeForever {
            //_account.value = it
        //}
    }
    fun getUserAccountDetail(id: String){
        viewModelScope.launch {
            _account.value = dbServices.getNormalUserServices()?.getNormalUserAccountDetail(id)
            }
        }

    // Set lock button onclick to be observable
    private val _lockBtnClicked = MutableLiveData<Boolean>()
    val lockBtnClicked: LiveData<Boolean> = _lockBtnClicked

    fun onLockBtnClicked() {
        _lockBtnClicked.value = true
    }

    suspend fun updateLockAccount(status: String, id: String) {

        dbServices.getAccountServices()!!.updateLockUserAccount(status, id)
    }
}