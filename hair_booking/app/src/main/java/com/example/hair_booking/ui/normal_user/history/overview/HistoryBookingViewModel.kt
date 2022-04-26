package com.example.hair_booking.ui.normal_user.history.overview

import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch
class HistoryBookingViewModel: ViewModel() {
    private val _historyList = MutableLiveData<ArrayList<Appointment>>()
    val historyList:LiveData<ArrayList<Appointment>> = _historyList
    private val _isVisibleProgressBar = MutableLiveData<Boolean>()
    val isVisibleProgressBar:LiveData<Boolean> = _isVisibleProgressBar
    init {
        viewModelScope.launch {
            getHistoryList()
        }
    }
    suspend fun getHistoryList(status:String? = null){
        val currentUser = AuthRepository.getCurrentUser()
        try {
            _isVisibleProgressBar.value = true
            val listAppointment = dbServices.getAppointmentServices()?.getAppointmentListForUser(currentUser!!.email!!,status)
            _isVisibleProgressBar.value = false
            _historyList.value = listAppointment!!

        }catch (e:Exception){
            Log.e("huy-exception",e.message.toString(),e)
            throw e
        }
    }
    suspend fun cancelAppointment(adapter:HistoryListAdapter,position:Int,status:String? = null){
        val currentUser = AuthRepository.getCurrentUser()
        try {
            _isVisibleProgressBar.value = true
            val listAppointment = dbServices.getAppointmentServices()?.getAppointmentListForUser(currentUser!!.email!!,status)
            _isVisibleProgressBar.value = false
            adapter.notifyItemRemoved(position)
            _historyList.value = listAppointment!!

        }catch (e:Exception){
            Log.e("huy-exception",e.message.toString(),e)
            throw e
        }
    }


}