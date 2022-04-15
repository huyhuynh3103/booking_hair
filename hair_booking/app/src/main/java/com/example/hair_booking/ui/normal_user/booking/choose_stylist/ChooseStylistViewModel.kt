package com.example.hair_booking.ui.normal_user.booking.choose_stylist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.booking.BookingServices
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.services.local.StylistServices
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class ChooseStylistViewModel(private val chosenSalonId: MutableLiveData<String>,
                             private val chosenShiftId: MutableLiveData<String>,
                             private val chosenTimeRange: MutableLiveData<Pair<Float, Float>>,
                             private val chosenDate: MutableLiveData<String>) : ViewModel() {
    private val _stylistList: MutableLiveData<ArrayList<Stylist>> = MutableLiveData<ArrayList<Stylist>>()
    val stylistList: LiveData<ArrayList<Stylist>> = _stylistList

    init {
        viewModelScope.launch {
            getStylistList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun getStylistList() {
//        val stylistList: ArrayList<Stylist>? = dbServices.getStylistServices()?.findAll()
//
//        val stylistsToDisplay: ArrayList<Stylist> = ArrayList(stylistList)

//        if (stylistList != null) {
//            for(stylist in stylistList) {
//                if(stylist.workPlace?.id == chosenSalonId.value) {
//                    if((stylist.shifts?.get("morning") as HashMap<*, *>).containsValue(chosenShiftId.value)
//                        || (stylist.shifts?.get("afternoon") as HashMap<*, *>).containsValue(chosenShiftId.value)
//                        || (stylist.shifts?.get("evening") as HashMap<*, *>).containsValue(chosenShiftId.value)) {
//                        stylistsToDisplay.add(Stylist(stylist))
//                    }
//                }
//            }
//        }

        val busyStylistsIds: ArrayList<String> = BookingServices.getIdOfBusyStylistsAtSpecificTime(
            chosenTimeRange.value!!,
            chosenSalonId.value!!,
            chosenDate.value!!,
            chosenShiftId.value!!)

        val allStylist: ArrayList<Stylist> = dbServices.getStylistServices()!!.findAll()

        var availableStylist: ArrayList<Stylist> = ArrayList(allStylist)
        availableStylist.removeIf { stylist ->
            busyStylistsIds.contains(stylist.id)
                    || stylist.workPlace!!.id != chosenSalonId.value
                    || !StylistServices.isWorking(chosenShiftId.value!!, stylist.shifts!!)
        }

        // Add default option
        availableStylist.add(0, Stylist(availableStylist[0].id, "Mặc định", "none", ""))
        _stylistList.value = availableStylist
    }


}