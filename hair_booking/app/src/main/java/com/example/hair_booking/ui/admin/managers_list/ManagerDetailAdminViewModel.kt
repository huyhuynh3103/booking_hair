package com.example.hair_booking.ui.admin.managers_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class ManagerDetailAdminViewModel: ViewModel() {
    private val _account: MutableLiveData<Account> = MutableLiveData()
    val account: LiveData<Account> = _account

    private val _salonList: MutableLiveData<ArrayList<Salon>> = MutableLiveData()
    val salonList: LiveData<ArrayList<Salon>> = _salonList

    init {
        viewModelScope.launch {
            getSalonList()
        }
    }

    fun getAccountDetail(id: String){
        viewModelScope.launch {
            _account.value = dbServices.getAccountServices()?.accountManagerDetail(id)
        }
    }

    // Set lock button onclick to be observable
    private val _lockBtnClicked = MutableLiveData<Boolean>()
    val lockBtnClicked: LiveData<Boolean> = _lockBtnClicked

    fun onLockBtnClicked() {
        _lockBtnClicked.value = true
    }

    // Set save button onclick to be observable
    private val _saveBtnClicked = MutableLiveData<Boolean>()
    val saveBtnClicked: LiveData<Boolean> = _saveBtnClicked

    fun onSaveBtnClicked() {
        _saveBtnClicked.value = true
    }

    suspend fun updateLockAccount(id: String) {
        dbServices.getAccountServices()!!.updateLockManagerAccount(id)
    }

    private suspend fun getSalonList() {
        _salonList.value = dbServices.getSalonServices()?.FindAll()
    }

    suspend fun updateManager(position: Int, id: String) {
        var selectedID: String = ""
        val list = salonList.value

        for (i in list?.indices!!) {
            if (i == position) {
                selectedID = list[i].id
            }
        }

        dbServices.getAccountServices()?.updateManager(selectedID, id)
    }
}