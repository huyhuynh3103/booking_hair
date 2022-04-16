package com.example.hair_booking.ui.admin.users_list

import android.util.Log
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
    private val _account: MutableLiveData<Account> = MutableLiveData()
    val account: LiveData<Account> = _account
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
            Log.d("tdht1", "get user detail")
        }
        //dbServices.getNormalUserServices()?.getNormalUserAccountDetail(id)?.observeForever {
            //_account.value = it
        //}
    }
    fun getUserAccountDetail(id: String){
        viewModelScope.launch {
            _account.value = dbServices.getNormalUserServices()?.getNormalUserAccountDetail(id)
            }
            Log.d("tdht1", "get account detail")
        }

    suspend fun getAccountList() {
        dbServices.getAccountServices()?.getAccountListForManagement()?.observeForever { accountList ->
            _accountList.value = accountList
        }
    }
}