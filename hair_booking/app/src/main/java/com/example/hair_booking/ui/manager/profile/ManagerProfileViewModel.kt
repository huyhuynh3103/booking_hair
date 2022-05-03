package com.example.hair_booking.ui.manager.profile

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
            _account.value = dbServices.getAccountServices()!!.getUserAccountByEmail(currentUserEmail!!)
            //_account.value = dbServices.getNormalUserServices()?.getNormalUserAccountDetail(id)
        }
    }

    private suspend fun getSalonList() {
        dbServices.getSalonServices()?.findAll()?.observeForever {
            _salonList.value = it
        }
    }


}