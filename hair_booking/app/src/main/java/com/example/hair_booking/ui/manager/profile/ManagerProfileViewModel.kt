package com.example.hair_booking.ui.manager.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class ManagerProfileViewModel: ViewModel() {
    private val _account: MutableLiveData<Account> = MutableLiveData()
    val account: LiveData<Account> = _account

    private val _salonList: MutableLiveData<ArrayList<Salon>> = MutableLiveData()
    val salonList: LiveData<ArrayList<Salon>> = _salonList

    init {
        viewModelScope.launch {
            getSalonList()
        }
    }

    fun getUserAccountDetail(){
        viewModelScope.launch {
            val currentUserEmail: String? = AuthRepository.getCurrentUser()!!.email
            if(currentUserEmail != null) {
                val currentUser = dbServices.getAccountServices()!!.getManagerAccountByEmail(currentUserEmail)
                if(currentUser != null) {
                    _account.value = currentUser
                }

            }


            //_account.value = dbServices.getNormalUserServices()?.getNormalUserAccountDetail(id)
        }
    }

    private suspend fun getSalonList() {
        _salonList.value = dbServices.getSalonServices()?.FindAll()
    }


}