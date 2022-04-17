package com.example.hair_booking.ui.manager.stylist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileDescriptor

class ManagerStylistDetailViewModel() : ViewModel() {
    private val _stylist: MutableLiveData<Stylist> = MutableLiveData()
    private val _salonList: MutableLiveData<ArrayList<Salon>> = MutableLiveData()

    val stylist: LiveData<Stylist> = _stylist
    val salonList: LiveData<ArrayList<Salon>> = _salonList

    init {
        getSalonList()
    }

    private fun getSalonList() {
        dbServices.getSalonServices()?.getSalonListForBooking()?.observeForever {
            _salonList.value = it
        }
    }

    fun getStylistDetail(id: String) {
        dbServices.getStylistServices()?.getStylistDetail(id)?.observeForever {
            _stylist.value = it
        }
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

    suspend fun updateStylist(id: String, stylist: Stylist) {
        dbServices.getStylistServices()?.updateOne(id, stylist)
    }
}