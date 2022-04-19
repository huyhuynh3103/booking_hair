package com.example.hair_booking.ui.normal_user.booking

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Shift
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.booking.TimeServices
import com.example.hair_booking.services.booking.BookingServices
import com.example.hair_booking.services.db.dbServices
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BookingViewModel: ViewModel() {
    private var _userId: MutableLiveData<String> = MutableLiveData<String>() // Used to query database
    val userId: LiveData<String> get() = _userId // Getter

    private var _userDiscountPoint: MutableLiveData<Long> = MutableLiveData<Long>() // Used to query database
    val userDiscountPoint: LiveData<Long> get() = _userDiscountPoint // Getter

    private lateinit var _salonId: String // Used to query database
    val salonId: String get() = _salonId // Getter
    private val _salonLocation = MutableLiveData<String>()
    val salonLocation: LiveData<String> = _salonLocation

    private var _serviceId: MutableLiveData<String> = MutableLiveData()// Used to query database
    private val _serviceTitle = MutableLiveData<String>()
    private val _servicePrice = MutableLiveData<Long>()
    val serviceId: LiveData<String> = _serviceId
    val serviceTitle: LiveData<String> = _serviceTitle
    val servicePrice: LiveData<Long> = _servicePrice

    private var stylistId: String = "" // Used to query database
    private val _stylist = MutableLiveData<String>()
    val stylist: LiveData<String> = _stylist

    private val _bookingDate = MutableLiveData<String>()
    val bookingDate: LiveData<String> = _bookingDate

    private var _shiftId: MutableLiveData<String> = MutableLiveData() // Used to query database
    val shiftId: LiveData<String> = _shiftId // getter

    private val _bookingTime = MutableLiveData<String>()
    val bookingTime: LiveData<String> = _bookingTime

    private var discountId: String = ""// Used to query database
    private val _discount = MutableLiveData<String>()
    private val _discountPercent = MutableLiveData<Float>()
    val discount: LiveData<String> = _discount
    val discountPercent: LiveData<Float> = _discountPercent

    private val _note = MutableLiveData<String>()
    val note: LiveData<String> = _note

    private val _totalPrice = MutableLiveData<Long>()
    val totalPrice: LiveData<Long> = _totalPrice


    init {
        viewModelScope.launch {
            getCurrentUserInfo()
        }
    }


    private suspend fun getCurrentUserInfo() {
        val currentUserEmail: String? = AuthRepository.getCurrentUser()!!.email
        if(currentUserEmail != null) {
            val accountId: String? = dbServices.getAccountServices()!!.getAccountByEmail(currentUserEmail)?.id

            if(accountId != null) {
                val currentUser = dbServices.getNormalUserServices()!!.getUserByAccountId(accountId)
                _userId.value = currentUser?.id
                _userDiscountPoint.value = currentUser?.discountPoint
            }
        }
    }

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


    // Set service edit text onclick to be observable
    private val _serviceEditTextClicked = MutableLiveData<Boolean>()
    val serviceEditTextClicked: LiveData<Boolean> = _serviceEditTextClicked
    fun onServiceEditTextClicked() {
        _serviceEditTextClicked.value = true
    }

    fun setChosenService(serviceId: String, serviceName: String, servicePrice: Long) {
        this._serviceId.value = serviceId
        _serviceTitle.value = serviceName
        _servicePrice.value = servicePrice
        _totalPrice.value = servicePrice
    }

    suspend fun getChosenServiceDuration(): Int {
        return dbServices.getServiceServices()!!.getServiceDuration(_serviceId.value!!)
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
        _discount.value = "" // Empty discount when re select date
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
        Log.d("bookac", "in bookviewmodel: $shiftId")
        this._shiftId.value = shiftId
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

    fun setChosenDiscount(discountId: String, discountTitle: String, discountPercent: Float) {
        this.discountId = discountId
        _discount.value = discountTitle
        _discountPercent.value = discountPercent
        _totalPrice.value = calculateTotal(servicePrice.value!!, discountPercent)
    }

    fun calculateTotal(servicePrice: Long, discountPercent: Float): Long {
        return (servicePrice - (servicePrice * discountPercent)).toLong()
    }

    // Set confirm button onclick to be observable
    private val _confirmBtnClicked = MutableLiveData<Boolean>()
    val confirmBtnClicked: LiveData<Boolean> = _confirmBtnClicked
    fun onConfirmBtnClicked() {
        _confirmBtnClicked.value = true
    }

    // Check if there are empty fields
    fun checkEmptyFieldsExist(): Boolean {
        return salonLocation.value.isNullOrEmpty() || serviceTitle.value.isNullOrEmpty()
                 || bookingDate.value.isNullOrEmpty()
                || bookingTime.value.isNullOrEmpty()
    }

    fun setupShiftPickerSpinner(context: Activity,
                                shiftPickerSpinner: Spinner,
                                timePickerLabel: TextView,
                                timePickerSpinner: Spinner,
                                chooseStylistLabel: TextView,
                                chooseStylistTextInputLayout: TextInputLayout,) {
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
                    if(TimeServices.isLargerThan(TimeServices.toBigDecimal(currentTime), shift.endHour!!.toBigDecimal())) {
                        shiftToBeDisabled.add(result.indexOf(shift))
                        Log.d("xk", result.indexOf(shift).toString() + "1231")
                    }
                }

            }

            // Assign adapter to spinner
            shiftPickerSpinner.adapter = ShiftPickerSpinnerAdapter(context, shiftItems, shiftToBeDisabled)

            shiftPickerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if(position - 1 >= 0) {
                        setChosenShift(result[position - 1].id)
                        viewModelScope.launch {
                            setupTimePickerSpinner(context, timePickerLabel, timePickerSpinner, result[position - 1], chooseStylistLabel, chooseStylistTextInputLayout)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }



    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun setupTimePickerSpinner(context: Activity,
                                               timePickerLabel: TextView,
                                               timePickerSpinner: Spinner,
                                               shift: Shift,
                                               chooseStylistLabel: TextView,
                                               chooseStylistTextInputLayout: TextInputLayout,) {
        viewModelScope.launch {
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
                if(_bookingDate.value != null) {
                    disabledTimePositions = BookingServices.getDisabledTimePositions(
                        chosenServiceDuration,
                        availableTime,
                        _salonId,
                        _bookingDate.value!!,
                        _shiftId.value!!, isToday())

                }
            }.await()
            availableTime.add(0, "Chọn giờ")
            for(i in 1 until availableTime.size) {
                if(disabledTimePositions.contains(i))
                    availableTime[i] = availableTime[i] + " (hết chỗ)"
            }
            // Assign adapter to spinner
            timePickerSpinner.adapter = TimePickerSpinnerAdapter(context, disabledTimePositions, availableTime)

            if(timePickerLabel.visibility == View.GONE && timePickerSpinner.visibility == View.GONE) {
                timePickerLabel.visibility = View.VISIBLE
                timePickerSpinner.visibility = View.VISIBLE
            }

            timePickerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if(position - 1 >= 0) {
                        _bookingTime.value = availableTime[position].replace(':', '.')
                        if(chooseStylistLabel.visibility == View.GONE && chooseStylistTextInputLayout.visibility == View.GONE) {
                            chooseStylistLabel.visibility = View.VISIBLE
                            chooseStylistTextInputLayout.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun saveBookingSchedule(note: String): HashMap<String, *>? {
        // Re check if stylist is free
        // In case there is someone has just booked with the chosen stylist at the chosen date and time
        val chosenTimeRange = getTimeRangeChosenInHour()
        var availableStylist: ArrayList<Stylist> = BookingServices.getAvailableStylists(
            chosenTimeRange,
            salonId,
            bookingDate.value!!,
            shiftId.value!!)

        if(availableStylist.size <= 0)
            return null // Announce to user

        if(stylistId.isNullOrEmpty()) {
            // Select default stylist
            stylistId = availableStylist[0].id
        }

        if(discountId.isNullOrEmpty() || discount.value.isNullOrEmpty()) {
            discountId = ""
            _discount.postValue("")
        }


        return dbServices.getAppointmentServices()!!.saveBookingSchedule(
            userId.value!!,
            salonId,
            _serviceId.value!!,
            serviceTitle.value!!,
            stylistId,
            bookingDate.value!!,
            bookingTime.value!!,
            shiftId.value!!,
            discountId,
            discount.value!!,
            note,
            totalPrice.value!!
        )
    }

    suspend fun getTimeRangeChosenInHour(): Pair<Float, Float> {
        val bookingTimeToDisplay: Float = bookingTime.value?.replace(':', '.')?.toFloat() ?: 0.0F
        val bookingTimeInHour: Float = TimeServices.timeToDisplayToTimeInHour(bookingTimeToDisplay)

        val chosenServiceDurationInHour: Float = TimeServices.minuteToHour(getChosenServiceDuration())

        // estimatedEndTime = booking time + service duration
        val estimatedEndTime: Float = TimeServices.addTimeInHour(bookingTimeInHour, chosenServiceDurationInHour)
        return Pair(bookingTimeInHour, estimatedEndTime)
    }
}