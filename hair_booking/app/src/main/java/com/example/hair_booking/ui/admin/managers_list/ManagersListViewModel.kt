package com.example.hair_booking.ui.admin.managers_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class ManagersListViewModel: ViewModel() {
    private val _accountList: MutableLiveData<ArrayList<Account>> = MutableLiveData()

    val accountList: LiveData<ArrayList<Account>> = _accountList


    init {
        viewModelScope.launch {
            getAccountList()
        }

    }

    suspend fun getAccountList() {
        dbServices.getAccountServices()?.getAccountListForManagement()?.observeForever { accountList ->
            _accountList.value = accountList
        }
    }

}