package com.example.hair_booking.ui.normal_user.history.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.Constant
import com.example.hair_booking.model.*
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch

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

    private val _rating = MutableLiveData<Float>()
    val rating:LiveData<Float> = _rating

    private val _ratingContent = MutableLiveData<String>()
    val ratingContent:LiveData<String> = _ratingContent

    private val _ratingIndicator = MutableLiveData<Boolean>()
    val ratingIndicator:LiveData<Boolean> = _ratingIndicator

    private val _isVisibleSendRating = MutableLiveData<Boolean>()
    val isVisibleSendRating:LiveData<Boolean> = _isVisibleSendRating

    init {
        _ratingIndicator.value = false
        _isVisibleSendRating.value = false
        _rating.value = Float.fromBits(0)
        _ratingContent.value = "Gửi đánh giá của bạn đến chúng tôi"
        _appointmentDiscount.value = "Không có"
        _priceDiscount.value = "0"
        _appointmentStylist.value = "Không có"
        _appointmentNote.value = "Không có"
        viewModelScope.launch {
            prepareData()
        }
    }
    fun rating(rate:Float){
        if(rate!=_rating.value){
            _isVisibleSendRating.value = true
        }
        _rating.value = rate
        if(_rating.value != Float.fromBits(0)){
            if(rate>4){
                _ratingContent.value = Constant.rating.great
            }
            else if(rate>3){
                _ratingContent.value = Constant.rating.good
            }
            else if(rate>2){
                _ratingContent.value = Constant.rating.normal
            }
            else if(rate>1){
                _ratingContent.value = Constant.rating.bad
            }
            else{
                _ratingContent.value = Constant.rating.veryBad
            }
        }
    }
    fun setIndicator(bool:Boolean){
        _ratingIndicator.value = bool
    }
    suspend fun prepareData(){
        val appointment = dbServices.getAppointmentServices()!!.getAppointmentById(id.value!!)
        if(appointment!=null){
            val rate = appointment.rate
            _rating.value = rate!!.toFloat()
            if(_rating.value != Float.fromBits(0)){
                _ratingIndicator.value = true
                _isVisibleSendRating.value = false
                if(rate>4){
                    _ratingContent.value = Constant.rating.great
                }
                else if(rate>3){
                    _ratingContent.value = Constant.rating.good
                }
                else if(rate>2){
                    _ratingContent.value = Constant.rating.normal
                }
                else if(rate>1){
                    _ratingContent.value = Constant.rating.bad
                }
                else{
                    _ratingContent.value = Constant.rating.veryBad
                }
            }
            else{
                _ratingIndicator.value = false
            }



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
                salonQuery!!["address"] as HashMap<String, String>
            )
            _salonName.value = salon.name
            _salonAddress.value = salon.addressToString()
            if(salonQuery!!["phoneNumber"]!=null){
                _salonPhoneNumber.value = salonQuery!!["phoneNumber"].toString()
            }




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