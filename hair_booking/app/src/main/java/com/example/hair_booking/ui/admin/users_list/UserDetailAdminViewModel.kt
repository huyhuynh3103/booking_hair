package com.example.hair_booking.ui.admin.users_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class UserDetailAdminViewModel: ViewModel() {
    private val _user: MutableLiveData<NormalUser> = MutableLiveData()
    val user: LiveData<NormalUser> = _user
    private val _accountList: MutableLiveData<ArrayList<Account>> = MutableLiveData()
    val accountList: LiveData<ArrayList<Account>> = _accountList

    init {
        viewModelScope.launch {
            getAccountList()
        }

    }

    fun getUserDetail(id: String){
        dbServices.getNormalUserServices()?.getNormalUserDetail(id)?.observeForever {
            _user.value = it
        }
    }
    suspend fun getAccountList() {
        dbServices.getAccountServices()?.getAccountListForManagement()?.observeForever { accountList ->
            _accountList.value = accountList
        }
    }
}