package com.example.hair_booking.ui.admin.users_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

class UsersListViewModel: ViewModel() {
    private var _userList = MutableLiveData<ArrayList<NormalUser>>()

    val userList: LiveData<ArrayList<NormalUser>> = _userList


    init {
        viewModelScope.launch {
            getUserList()
        }

    }

    suspend fun getUserList() {
        // getUserList() will return mutable live data
        // => we need to observe it to catch the return when database has finished querying
        dbServices.getNormalUserServices()?.getUserListForManagement()
            ?.observeForever{ userList ->
                _userList.value = userList
            }
    }

}