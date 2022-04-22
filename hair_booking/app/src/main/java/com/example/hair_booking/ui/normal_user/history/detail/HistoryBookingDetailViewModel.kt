package com.example.hair_booking.ui.normal_user.history.detail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.*
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class HistoryBookingDetailViewModel(private val id: MutableLiveData<String>): ViewModel() {

    private val _appointmentSubId = MutableLiveData<String>()
    val appointmentSubId:LiveData<String> = _appointmentSubId

    private val _appointmentTime = MutableLiveData<String>()
    val appointmentTime:LiveData<String> = _appointmentTime

    private val _appointmentStatus = MutableLiveData<String>()
    val appointmentStatus:LiveData<String> = _appointmentStatus

    private val _appointmentService = MutableLiveData<String>()
    val appointmentService:LiveData<String> = _appointmentService

    private val _appointmentNote = MutableLiveData<String>()
    val appointmentNote:LiveData<String> = _appointmentNote

    private val _appointmentStylist = MutableLiveData<String>()
    val appointmentStylist:LiveData<String> = _appointmentStylist

    private val _appointmentDiscount = MutableLiveData<String>()
    val appointmentDiscount:LiveData<String> = _appointmentDiscount

    private val _salonName = MutableLiveData<String>()
    val salonName:LiveData<String> = _salonName

    private val _salonAddress = MutableLiveData<String>()
    val salonAddress:LiveData<String> = _salonAddress

    private val _salonPhoneNumber = MutableLiveData<String>()
    val salonPhoneNumber:LiveData<String> = _salonPhoneNumber

    private val _priceService = MutableLiveData<String>()
    val priceService:LiveData<String> = _priceService

    private val _priceDiscount = MutableLiveData<String>()
    val priceDiscount:LiveData<String> = _priceDiscount

    private val _totalPrice = MutableLiveData<String>()
    val totalPrice:LiveData<String> = _totalPrice



    init {
        _appointmentDiscount.value = "Không có"
        _priceDiscount.value = "0"
        _appointmentStylist.value = "Không có"
        _appointmentNote.value = "Không có"
        viewModelScope.launch {
            prepareData()
        }
    }

    suspend fun prepareData(){
        val appointment = dbServices.getAppointmentServices()!!.getAppointmentById(id.value!!)
        if(appointment!=null){
            _appointmentSubId.value = appointment.appointmentSubId
            _appointmentTime.value = appointment.bookingTime +" - "+appointment.bookingDate
            if(appointment.note!=null){
                _appointmentNote.value = appointment.note
            }
            _appointmentStatus.value = appointment.status
            val totalPrice = appointment.totalPrice
            _totalPrice.value = appointment.totalPrice.toString()

            val salonQuery = appointment.hairSalon
            val salonRef = salonQuery?.get("id") as DocumentReference?
            val salon = Salon(
                salonRef!!.id,
                salonQuery!!["name"] as String,
                salonQuery!!["address"] as HashMap<String, String>,
                salonQuery!!["phoneNumber"] as String
            )
            _salonName.value = salon.name
            _salonAddress.value = salon.addressToString()
            _salonPhoneNumber.value = salon.phoneNumber.toString()



            val serviceQuery = appointment.service
            _appointmentService.value = serviceQuery!!["title"].toString()+" - "+serviceQuery["duration"].toString()
            _priceService.value = serviceQuery["price"].toString()


            val stylistQuery = appointment.stylist
            _appointmentStylist.value = stylistQuery?.get("fullName").toString()


            val discountQuery = appointment.discountApplied
            val discountRef = discountQuery?.get("id") as DocumentReference?
            val idDiscount = discountRef?.id
            if(idDiscount!=null){
                val discount = dbServices.getDiscountServices()?.getDiscountById(idDiscount)
                _appointmentDiscount.value = discount?.title.toString()
                discount?.percent?.let {
                    _priceDiscount.value = (it * totalPrice!!).toString()
                }
            }
        }
        else{
            Log.e("Exception","Not existing appointment $id in database")
        }
    }

}