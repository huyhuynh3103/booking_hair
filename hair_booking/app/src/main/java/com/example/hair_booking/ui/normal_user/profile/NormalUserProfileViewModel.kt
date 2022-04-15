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
    private val _accountList: MutableLiveData<ArrayList<Account>> = MutableLiveData()
    val accountList: LiveData<ArrayList<Account>> = _accountList

    init {
        viewModelScope.launch {
            getAccountList()
        }

    }

    fun getNormalUserDetail(id: String){
        dbServices.getNormalUserServices()?.getNormalUserDetail(id)?.observeForever {
            _normalUser.value = it
        }
    }

    suspend fun getAccountList() {
        dbServices.getAccountServices()?.getAccountListForManagement()?.observeForever { accountList ->
            _accountList.value = accountList
        }
    }
}