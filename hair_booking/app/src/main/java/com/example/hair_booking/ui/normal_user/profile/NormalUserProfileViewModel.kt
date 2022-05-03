package com.example.hair_booking.ui.normal_user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class NormalUserProfileViewModel: ViewModel() {
    private val _normalUser: MutableLiveData<NormalUser> = MutableLiveData()
    val normalUser: LiveData<NormalUser> = _normalUser
    private val _account: MutableLiveData<Account> = MutableLiveData()
    val account: LiveData<Account> = _account

    //init {
        //viewModelScope.launch {
            //getCurrentUserInfo()
        //}
    //}

    suspend fun getNormalUserDetail(){
        val currentUserEmail: String? = AuthRepository.getCurrentUser()!!.email
        if(currentUserEmail != null) {
            val currentUser =
                dbServices.getAccountServices()!!.getUserAccountByEmail(currentUserEmail)
            if (currentUser != null) {
                val currentNormalUser =
                    dbServices.getNormalUserServices()!!.getUserByAccountId(currentUser.id)
                _normalUser.postValue(currentNormalUser)
            }
        }
        //_normalUser.postValue(dbServices.getNormalUserServices()?.getUserById(id))
    }

    fun getUserAccountDetail(){
        viewModelScope.launch {
            val currentUserEmail: String? = AuthRepository.getCurrentUser()!!.email
            _account.value = dbServices.getAccountServices()!!.getUserAccountByEmail(currentUserEmail!!)
            //_account.value = dbServices.getNormalUserServices()?.getNormalUserAccountDetail(id)
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