package com.example.hair_booking.ui.admin.managers_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class ManagerDetailAdminViewModel: ViewModel() {
    private val _account: MutableLiveData<Account> = MutableLiveData()
    val account: LiveData<Account> = _account


    fun getAccountDetail(id: String){
        viewModelScope.launch {
            _account.value = dbServices.getAccountServices()?.accountDetail(id)
        }
    }

    // Set lock button onclick to be observable
    private val _lockBtnClicked = MutableLiveData<Boolean>()
    val lockBtnClicked: LiveData<Boolean> = _lockBtnClicked

    fun onLockBtnClicked() {
        _lockBtnClicked.value = true
    }

    suspend fun updateLockAccount(id: String) {

        dbServices.getAccountServices()!!.updateLockManagerAccount(id)
    }
}