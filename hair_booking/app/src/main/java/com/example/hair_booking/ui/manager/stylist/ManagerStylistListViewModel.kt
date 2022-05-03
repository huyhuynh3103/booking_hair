package com.example.hair_booking.ui.manager.stylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch

class ManagerStylistListViewModel: ViewModel() {
    private val _manager: MutableLiveData<Account> = MutableLiveData()
    private val _stylistList: MutableLiveData<ArrayList<Stylist>> = MutableLiveData<ArrayList<Stylist>>()
    val manager: LiveData<Account> = _manager
    val stylistList: LiveData<ArrayList<Stylist>> = _stylistList

    suspend fun getManagerDetail(id: String) {
        _manager.value = dbServices.getAccountServices()?.findById(id)
    }

    suspend fun getStylistList(salon: DocumentReference?) {
        _stylistList.value = dbServices.getStylistServices()?.findAll(salon)
    }

    suspend fun getUpdatedStylistList(salon: DocumentReference?) {
        _stylistList.value = dbServices.getStylistServices()?.findAll(salon)
    }
    suspend fun getCurrentUserInfo() : String {
        val currentUserEmail: String? = AuthRepository.getCurrentUser()!!.email
        if(currentUserEmail != null) {
            val currentUser = dbServices.getAccountServices()!!.getManagerAccountByEmail(currentUserEmail)
            if(currentUser != null) {
                return currentUser.id
            }
        }
        return ""
    }
}