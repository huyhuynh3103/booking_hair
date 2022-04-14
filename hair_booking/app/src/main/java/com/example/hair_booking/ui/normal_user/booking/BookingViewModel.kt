package com.example.hair_booking.ui.normal_user.booking

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Shift
import com.example.hair_booking.services.booking.BigDecimalTimeServices
import com.example.hair_booking.services.booking.BookingServices
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class BookingViewModel: ViewModel() {
    private lateinit var _salonId: String // Used to query database
    val salonId: String get() = _salonId // Getter
    private val _salonLocation = MutableLiveData<String>()
    val salonLocation: LiveData<String> = _salonLocation

    private var serviceId: String = ""// Used to query database
    private val _service = MutableLiveData<String>()
    val service: LiveData<String> = _service

    private lateinit var stylistId: String // Used to query database
    private val _stylist = MutableLiveData<String>()
    val stylist: LiveData<String> = _stylist

    private val _bookingDate = MutableLiveData<String>()
    val bookingDate: LiveData<String> = _bookingDate

    private var _shiftId: String = "" // Used to query database
    val shiftId: String = _shiftId // getter

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

    suspend fun getChosenServiceDuration(): Int {
        return dbServices.getServiceServices()!!.getServiceDuration(serviceId)
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

    fun isToday(): Boolean {
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

    fun setChosenShift(shiftId: String) {
        this._shiftId = shiftId
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

    fun setupShiftPickerSpinner(context: Activity,
                                shiftPickerSpinner: Spinner,
                                timePickerLabel: TextView,
                                timePickerSpinner: Spinner) {
        var shiftItems: ArrayList<String> = arrayListOf("Chọn ca hớt tóc") // Init with a placeholder first
        var shiftToBeDisabled: ArrayList<Int> = ArrayList()



        // Get shifts and observe forever to wait for callback when data return
        dbServices.getShiftServices()!!.getAllShifts()?.observeForever{ result ->
            // Get current time
            val now = Calendar.getInstance()
            val currentHourIn24Format: Int = now.get(Calendar.HOUR_OF_DAY) // return the hour in 24 hrs format (ranging from 0-23)
            val currentMinute: Int = now.get(Calendar.MINUTE) // return minute
            val currentTime = currentHourIn24Format + (currentMinute / 100.0F)

            for(shift in result) {
                // Shifts returned from database is an object
                // => need convert to a string for display
                // ex: "Sáng (08:00 - 12:00)"
                shiftItems.add(shift.toStringForBookingDisplay())

                if(isToday()) {
                    if(BigDecimalTimeServices.isLargerThan(BigDecimalTimeServices.toBigDecimal(currentTime), shift.endHour!!.toBigDecimal())) {
                        shiftToBeDisabled.add(result.indexOf(shift))
                        Log.d("xk", result.indexOf(shift).toString() + "1231")
                    }
                }

            }

            // Assign adapter to spinner
            shiftPickerSpinner.adapter = ShiftPickerSpinnerAdapter(context, shiftItems, shiftToBeDisabled)

//            val initPosition = shiftPickerSpinner.selectedItemPosition
//            shiftPickerSpinner.setSelection(initPosition, false)
//            Log.d("xk", "init pos selected $initPosition")
//            shiftPickerSpinner.onItemSelectedListener = null
            shiftPickerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    Log.d("xk", "onitemselected ${position - 1}")
                    if(position - 1 >= 0) {
                        setChosenShift(result[position - 1].id)
                        viewModelScope.launch {
                            setupTimePickerSpinner(context, timePickerLabel, timePickerSpinner, result[position - 1])
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }



    }

    private suspend fun setupTimePickerSpinner(context: Activity,
                                               timePickerLabel: TextView,
                                               timePickerSpinner: Spinner,
                                               shift: Shift) {
        viewModelScope.launch {
            Log.d("xk", "setup time picker")
            var availableTime: ArrayList<String> = ArrayList() // Init with empty array
            // get chosen service duration
            val chosenServiceDuration: Int = getChosenServiceDuration()

            // Get available time based on shift chosen
            availableTime.addAll(BookingServices.generateTimeBasedOnShift(shift, chosenServiceDuration, isToday()))


            var disabledTimePositions: ArrayList<Int> = ArrayList()
            async {
                // Get list of time that stylists are busy
                // The function will return an array contains position of element to be disabled in availableTime
                // Note: just disable, not remove => user can view all, including the disable ones
                Log.d("xk", "getting disable time")
                if(_bookingDate.value != null) {
                    disabledTimePositions = BookingServices.getDisabledTimePositions(
                        chosenServiceDuration,
                        availableTime,
                        _bookingDate.value!!,
                        _shiftId, isToday())

                }
                Log.d("xk", "finish getting disable time")
            }.await()
            Log.d("xk", "finish getting disable time1")
            // Add placeholder
            Log.d("xk", "placehd")
            availableTime.add(0, "Chọn giờ")
            availableTime.forEach {
                Log.d("xk", "reconstruct time picker")
                Log.d("xk", it)
            }
            // Assign adapter to spinner
            timePickerSpinner.adapter = TimePickerSpinnerAdapter(context, disabledTimePositions, availableTime)

            if(timePickerLabel.visibility == View.GONE && timePickerSpinner.visibility == View.GONE) {
                timePickerLabel.visibility = View.VISIBLE
                timePickerSpinner.visibility = View.VISIBLE
            }
        }
    }

    fun saveBookingSchedule() {
        // Do sth
    }

}