package com.example.hair_booking.ui.manager.stylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch

class ManagerStylistDetailViewModel: ViewModel() {
    private val _stylist: MutableLiveData<Stylist> = MutableLiveData()
    private val _salonList: MutableLiveData<ArrayList<Salon>> = MutableLiveData()
    private val _account: MutableLiveData<Account> = MutableLiveData()

    val stylist: LiveData<Stylist> = _stylist
    val salonList: LiveData<ArrayList<Salon>> = _salonList
    val account: LiveData<Account> = _account

    init {
        viewModelScope.launch {
            getSalonList()
        }
    }

    private suspend fun getSalonList() {
        dbServices.getSalonServices()?.findAll()?.observeForever {
            _salonList.value = it
        }
    }

    suspend fun getStylistDetail(id: String) {
        _stylist.value = dbServices.getStylistServices()?.findById(id)
    }

    suspend fun getManagerAccount(email: String) {
        _account.value = dbServices.getAccountServices()?.getManagerAccountByEmail(email)
    }

    suspend fun getSelectedWorkplace(position: Int): DocumentReference? {
        var result: DocumentReference?
        var selectedID: String? = null
        val list = salonList.value

        for (i in list?.indices!!) {
            if (i == position) {
                selectedID = list[i].id
            }
        }

        result = dbServices.getSalonServices()?.getWorkplace(selectedID)

        return result
    }

    suspend fun getShiftRef(id: String): DocumentReference? {
        return dbServices.getShiftServices()?.getShiftRef(id)
    }

    suspend fun getStylistRef(id: String): DocumentReference? {
        return dbServices.getStylistServices()?.getStylistRef(id)
    }

    suspend fun isBooked(stylistRef: DocumentReference?): Boolean {
        return dbServices.getAppointmentServices()?.countAppointment(stylistRef)!! > 0
    }

    suspend fun isBooked(stylistRef: DocumentReference?, shiftRef: DocumentReference?): Boolean {
        return dbServices.getAppointmentServices()?.countAppointment(stylistRef, shiftRef)!! > 0
    }

    suspend fun updateStylist(id: String, stylist: Stylist) {
        dbServices.getStylistServices()?.updateOne(id, stylist)
    }

    suspend fun addStylist(stylist: Stylist) {
        dbServices.getStylistServices()?.add(stylist)
    }

    suspend fun deleteStylist(id: String) {
        dbServices.getStylistServices()?.delete(id)
    }
}