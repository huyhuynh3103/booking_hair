package com.example.hair_booking.ui.normal_user.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class BookingViewModel: ViewModel() {
    private lateinit var _salonId: String // Used to query database
    val salonId: String get() = _salonId // Getter
    private val _salonLocation = MutableLiveData<String>()
    val salonLocation: LiveData<String> = _salonLocation

    private lateinit var serviceId: String // Used to query database
    private val _service = MutableLiveData<String>()
    val service: LiveData<String> = _service

    private lateinit var stylistId: String // Used to query database
    private val _stylist = MutableLiveData<String>()
    val stylist: LiveData<String> = _stylist

    private val _bookingDate = MutableLiveData<String>()
    val bookingDate: LiveData<String> = _bookingDate

    private val _bookingTime = MutableLiveData<String>()
    val bookingTime: LiveData<String> = _bookingTime

    private lateinit var discountId: String// Used to query database
    private val _discount = MutableLiveData<String>()
    val discount: LiveData<String> = _discount

    private val _note = MutableLiveData<String>()
    val note: LiveData<String> = _note

    // Set salon edit text onclick to be observable
    private val _salonEditTextClicked = MutableLiveData<Boolean>()
    val salonEditTextClicked: LiveData<Boolean> = _salonEditTextClicked
    fun onSalonEditTextClicked() {
        _salonEditTextClicked.value = true
    }

    fun setChosenSalon(salonId: String, salonLocation: String) {
        _salonId = salonId
        _salonLocation.value = salonLocation
    }

    fun checkIfSalonEditTextIsEmpty(): Boolean {
        if(salonLocation.value.isNullOrEmpty())
            return true
        return false
    }

//    private fun getBusinessHourOfChosenSalon(): ArrayList<String> {
//
//    }

    // Set service edit text onclick to be observable
    private val _serviceEditTextClicked = MutableLiveData<Boolean>()
    val serviceEditTextClicked: LiveData<Boolean> = _serviceEditTextClicked
    fun onServiceEditTextClicked() {
        _serviceEditTextClicked.value = true
    }

    fun setChosenService(serviceId: String, serviceName: String) {
        this.serviceId = serviceId
        _service.value = serviceName
    }

    // Set stylist edit text onclick to be observable
    private val _stylistEditTextClicked = MutableLiveData<Boolean>()
    val stylistEditTextClicked: LiveData<Boolean> = _stylistEditTextClicked
    fun onStylistEditTextClicked() {
        _stylistEditTextClicked.value = true
    }

    fun setChosenStylist(stylistId: String, stylistName: String) {
        this.stylistId = stylistId
        _stylist.value = stylistName
    }



    // Set date edit text onclick to be observable
    private val _dateEditTextClicked = MutableLiveData<Boolean>()
    val dateEditTextClicked: LiveData<Boolean> = _dateEditTextClicked
    fun onDateEditTextClicked() {
        _dateEditTextClicked.value = true
    }

    fun setChosenDate(dateChosen: String) {
        _bookingDate.value = dateChosen
    }

    fun isToday(dateToCheck: Calendar): Boolean {
        val now = Calendar.getInstance()

        // Convert "now" to string format
        val dayOfMonth = now[Calendar.DAY_OF_MONTH]
        var monthOfYear = now[Calendar.MONTH]
        val year = now[Calendar.YEAR]
        // Add leading zero if has
        val dayInString: String = if(dayOfMonth < 10) "0$dayOfMonth" else "" + dayOfMonth
        val monthInString: String = if(monthOfYear < 10) "0" + (monthOfYear + 1) else "" + dayOfMonth

        // Concatenate to a full date string
        val dateInStringFormat = "$dayInString/$monthInString/$year"

        if(dateInStringFormat == bookingDate.value)
            return true
        return false
    }

    // Set time edit text onclick to be observable
    private val _timeEditTextClicked = MutableLiveData<Boolean>()
    val timeEditTextClicked: LiveData<Boolean> = _timeEditTextClicked
    fun onTimeEditTextClicked() {
        _timeEditTextClicked.value = true
    }
    fun setChosenTime(timeChosen: String) {
        _bookingTime.value = timeChosen
    }


    // Set discount edit text onclick to be observable
    private val _discountEditTextClicked = MutableLiveData<Boolean>()
    val discountEditTextClicked: LiveData<Boolean> = _discountEditTextClicked
    fun onDiscountEditTextClicked() {
        _discountEditTextClicked.value = true
    }

    fun setChosenDiscount(discountId: String, discountTitle: String) {
        this.discountId = discountId
        _discount.value = discountTitle
    }

    // Set confirm button onclick to be observable
    private val _confirmBtnClicked = MutableLiveData<Boolean>()
    val confirmBtnClicked: LiveData<Boolean> = _confirmBtnClicked
    fun onConfirmBtnClicked() {
        _confirmBtnClicked.value = true
    }

    // Check if there are empty fields
    fun checkEmptyFieldsExist(): Boolean {
        return salonLocation.value.isNullOrEmpty() || service.value.isNullOrEmpty()
                || stylist.value.isNullOrEmpty() || bookingDate.value.isNullOrEmpty()
                || bookingTime.value.isNullOrEmpty()
    }

    fun saveBookingSchedule() {
        // Do sth
    }

}