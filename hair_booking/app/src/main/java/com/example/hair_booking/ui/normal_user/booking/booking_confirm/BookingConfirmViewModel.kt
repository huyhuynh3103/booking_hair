package com.example.hair_booking.ui.normal_user.booking.booking_confirm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.services.local.SalonServices
import kotlinx.coroutines.launch

class BookingConfirmViewModel(private val appointmentSaved: MutableLiveData<HashMap<String, *>?>) : ViewModel() {

    private val _hairSalonName: MutableLiveData<String> = MutableLiveData<String>()
    val hairSalonName: LiveData<String> = _hairSalonName

    private val _hairSalonAddress: MutableLiveData<String> = MutableLiveData<String>()
    val hairSalonAddress: LiveData<String> = _hairSalonAddress

    private val _serviceTitle: MutableLiveData<String> = MutableLiveData<String>()
    val serviceTitle: LiveData<String> = _serviceTitle

    private val _stylistFullName: MutableLiveData<String> = MutableLiveData<String>()
    val stylistFullName: LiveData<String> = _stylistFullName

    private val _bookingDateTime: MutableLiveData<String> = MutableLiveData<String>()
    val bookingDateTime: LiveData<String> = _bookingDateTime

    private val _discountTitle: MutableLiveData<String> = MutableLiveData<String>()
    val discountTitle: LiveData<String> = _discountTitle

    private val _note: MutableLiveData<String> = MutableLiveData<String>()
    val note: LiveData<String> = _note

    private val _totalPrice: MutableLiveData<String> = MutableLiveData<String>()
    val totalPrice: LiveData<String> = _totalPrice

    init {
        viewModelScope.launch {
            prepareBookingData()
        }
    }

    private fun prepareBookingData() {
        _hairSalonName.postValue(appointmentSaved.value?.get("hairSalonName") as String?)
        _hairSalonAddress.postValue(appointmentSaved.value?.get("hairSalonAddress") as String?)
        _serviceTitle.postValue(appointmentSaved.value?.get("serviceTitle") as String?)
        _stylistFullName.postValue("Stylist " + appointmentSaved.value?.get("stylist") as String?)
        val bookingDate: String = appointmentSaved.value?.get("bookingDate") as String
        var bookingTime: String = appointmentSaved.value?.get("bookingTime") as String
        bookingTime = bookingTime.replace('.', 'h')
        _bookingDateTime.postValue("$bookingTime Ngày $bookingDate")
        if(appointmentSaved.value?.get("discountApplied") != null)
            _discountTitle.postValue(appointmentSaved.value?.get("discountApplied") as String?)
        _note.postValue(appointmentSaved.value?.get("notes") as String?)
        _totalPrice.postValue((appointmentSaved.value?.get("totalPrice") as Long?).toString())
    }


}