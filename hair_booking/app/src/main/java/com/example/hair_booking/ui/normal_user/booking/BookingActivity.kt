package com.example.hair_booking.ui.normal_user.booking

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityBookingBinding
import com.example.hair_booking.services.booking.BookingServices
import com.example.hair_booking.ui.normal_user.booking.booking_confirm.BookingConfirmActivity
import com.example.hair_booking.ui.normal_user.booking.choose_discount.ChooseDiscountActivity
import com.example.hair_booking.ui.normal_user.booking.choose_salon.ChooseSalonActivity
import com.example.hair_booking.ui.normal_user.booking.choose_service.ChooseServiceActivity
import com.example.hair_booking.ui.normal_user.booking.choose_stylist.ChooseStylistActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap


class BookingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener{
    private val REQUEST_CODE_CHOOSE_SALON: Int = 1111
    private val REQUEST_CODE_CHOOSE_SERVICE: Int = 2222
    private val REQUEST_CODE_CHOOSE_STYLIST: Int = 3333
    private val REQUEST_CODE_CHOOSE_DISCOUNT: Int = 4444
    private val REQUEST_CODE_BOOKING_CONFIRM: Int = 5555
    private lateinit var binding: ActivityBookingBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: BookingViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        observeOnClickEvent()




    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun observeOnClickEvent() {
        // Observe salon edit text onclick event to perform navigation to choose salon screen
        viewModel.salonEditTextClicked.observe(this, androidx.lifecycle.Observer {
            moveToChooseSalonScreen()
        })

        // Observe service edit text onclick event to perform navigation to choose service screen
        viewModel.serviceEditTextClicked.observe(this, androidx.lifecycle.Observer {
            if(viewModel.checkIfSalonEditTextIsEmpty())
                displaySalonChosenRequiredWarning()
            else
                moveToChooseServiceScreen()
        })

        // Observe stylist edit text onclick event to perform navigation to choose stylist screen
        viewModel.stylistEditTextClicked.observe(this, androidx.lifecycle.Observer {
            if(viewModel.checkIfSalonEditTextIsEmpty())
                displaySalonChosenRequiredWarning()
            else {
                GlobalScope.launch {
                    moveToChooseStylistScreen()
                }
            }
        })

        // Observe date edit text onclick event to display date picker dialog
        viewModel.dateEditTextClicked.observe(this, androidx.lifecycle.Observer {
            if(viewModel.checkIfSalonEditTextIsEmpty())
                displaySalonChosenRequiredWarning()
            else
                displayDatePickerDialog()
        })


        // Observe discount edit text onclick event to perform navigation to choose discount screen
        viewModel.discountEditTextClicked.observe(this, androidx.lifecycle.Observer {
            moveToChooseDiscountScreen()
        })

        // Observe confirm button onclick event
        viewModel.confirmBtnClicked.observe(this, androidx.lifecycle.Observer {
            if(viewModel.checkEmptyFieldsExist())
                displayEmptyFieldsWarning()
            else {
                GlobalScope.launch {
                    val docSaved = viewModel.saveBookingSchedule(binding.note.text.toString())
                    if(docSaved.isNullOrEmpty()) {
                        runOnUiThread {
                            displayStylistBusyWarning()
                        }
                    }
                    else {
                        moveToBookingConfirmScreen(BookingServices.serializeAppointmentSaved(docSaved))
                    }
                }
            }
        })
    }

    private fun moveToBookingConfirmScreen(docSaved: HashMap<String, *>?) {
        val intent = Intent(this, BookingConfirmActivity::class.java)

        intent.putExtra("appointmentSaved", docSaved)

        startActivityForResult(intent, REQUEST_CODE_BOOKING_CONFIRM)
    }

    private fun moveToChooseSalonScreen() {
        val intent = Intent(this, ChooseSalonActivity::class.java)

        startActivityForResult(intent, REQUEST_CODE_CHOOSE_SALON)
    }

    private fun moveToChooseServiceScreen() {
        val intent = Intent(this, ChooseServiceActivity::class.java)

        startActivityForResult(intent, REQUEST_CODE_CHOOSE_SERVICE)
    }

    private suspend fun moveToChooseStylistScreen() {
        val intent = Intent(this, ChooseStylistActivity::class.java)

        intent.putExtra("chosenSalonId", viewModel.salonId)
        intent.putExtra("chosenShiftId", viewModel.shiftId.value)
        intent.putExtra("chosenDate", viewModel.bookingDate.value)
        val chosenTimeRange = viewModel.getTimeRangeChosenInHour()
        intent.putExtra("chosenTime", chosenTimeRange.first)
        intent.putExtra("estimatedEndTime", chosenTimeRange.second)
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_STYLIST)
    }

    private fun moveToChooseDiscountScreen() {
        if(viewModel.bookingDate.value.isNullOrEmpty())
            displayChosenDateRequiredWarning()
        else {
            val intent = Intent(this, ChooseDiscountActivity::class.java)

            intent.putExtra("userId", "lLed4Jd1HRPzEmwREbkl")
            intent.putExtra("chosenDate", viewModel.bookingDate.value)
            val userCurrentPoint: Long = 2500
            intent.putExtra("userCurrentPoint", userCurrentPoint)
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_DISCOUNT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) { // Handle the data from activity result based on request code
            REQUEST_CODE_CHOOSE_SALON -> {
                // Get chosen salon location returned from choose salon activity
                // Including salon id and salon location
                val salonId: String = data?.getStringExtra("salonId").orEmpty()
                val salonLocation: String = data?.getStringExtra("salonLocation").orEmpty()

                // Call viewmodel to set chosen salon
                if(salonId.isNotEmpty() && salonLocation.isNotEmpty())
                    binding.viewModel!!.setChosenSalon(salonId, salonLocation)
            }

            REQUEST_CODE_CHOOSE_SERVICE -> {
                // Get chosen service returned from choose service activity
                // Including service id and service name
                val serviceId: String = data?.getStringExtra("serviceId").orEmpty()
                val serviceName: String = data?.getStringExtra("serviceName").orEmpty()
                val servicePrice: Long? = data?.getLongExtra("servicePrice", 0)
                // Call viewmodel to set chosen service
                if(serviceId.isNotEmpty() && serviceName.isNotEmpty() && servicePrice != null)
                    binding.viewModel!!.setChosenService(serviceId, serviceName, servicePrice)

                displayDateTimePickerWrapper()
                // Setup list of shifts for user to choose after reselect service
                viewModel.setupShiftPickerSpinner(
                    this,
                    binding.shiftPickerSpinner,
                    binding.timePickerLabel,
                    binding.timePickerSpinner,
                    binding.chooseStylistLabel,
                    binding.chooseStylistTextInputLayout
                )
                hideTimePicker()
                displayTotalPrice()
            }

            REQUEST_CODE_CHOOSE_STYLIST -> {
                // Get chosen service returned from choose stylist activity
                // Including stylist id and name
                val stylistId: String = data?.getStringExtra("stylistId").orEmpty()
                val stylistName: String = data?.getStringExtra("stylistName").orEmpty()
                // Call viewmodel to set chosen stylist
                if(stylistId.isNotEmpty() && stylistName.isNotEmpty())
                    binding.viewModel!!.setChosenStylist(stylistId, stylistName)
            }

            REQUEST_CODE_CHOOSE_DISCOUNT -> {
                // Get chosen discount returned from choose discount activity
                // Including discount id and title
                val discountId: String = data?.getStringExtra("discountId").orEmpty()
                val discountTitle: String = data?.getStringExtra("discountTitle").orEmpty()
                val discountPercent: Float? = data?.getFloatExtra("discountPercent", 0.0F)
                // Call viewmodel to set chosen discount
                if(discountId.isNotEmpty() && discountTitle.isNotEmpty() && discountPercent != null)
                    binding.viewModel!!.setChosenDiscount(discountId, discountTitle, discountPercent)
            }
        }
    }

    private fun displayDateTimePickerWrapper() {
        if(binding.datePickerWrapper.visibility == View.GONE)
            binding.datePickerWrapper.visibility = View.VISIBLE
    }

    private fun displayShiftPicker() {
        if(binding.timePickerWrapper.visibility == View.GONE)
            binding.timePickerWrapper.visibility = View.VISIBLE
    }

    private fun displayTimePicker() {
        if(binding.timePickerLabel.visibility == View.GONE && binding.timePickerSpinner.visibility == View.GONE) {
            binding.timePickerLabel.visibility = View.VISIBLE
            binding.timePickerSpinner.visibility = View.VISIBLE
        }
    }

    private fun hideTimePicker() {
        if(binding.timePickerLabel.visibility == View.VISIBLE && binding.timePickerSpinner.visibility == View.VISIBLE) {
            binding.timePickerLabel.visibility = View.GONE
            binding.timePickerSpinner.visibility = View.GONE
        }
    }

    private fun hideChooseStylist() {
        if(binding.chooseStylistLabel.visibility == View.VISIBLE && binding.chooseStylistTextInputLayout.visibility == View.VISIBLE) {
            binding.chooseStylistLabel.visibility = View.GONE
            binding.chooseStylistTextInputLayout.visibility = View.GONE
        }
    }

    private fun hideTotalPrice() {
        if(binding.totalPriceLabel.visibility == View.VISIBLE
            && binding.totalPrice.visibility == View.VISIBLE
            && binding.vndUnit.visibility == View.VISIBLE) {
            binding.totalPriceLabel.visibility = View.GONE
            binding.totalPrice.visibility = View.GONE
            binding.vndUnit.visibility = View.GONE
        }
    }

    private fun displayTotalPrice() {
        if(binding.totalPriceLabel.visibility == View.GONE
            && binding.totalPrice.visibility == View.GONE
            && binding.vndUnit.visibility == View.GONE) {
            binding.totalPriceLabel.visibility = View.VISIBLE
            binding.totalPrice.visibility = View.VISIBLE
            binding.vndUnit.visibility = View.VISIBLE
        }
    }

    private fun displayDatePickerDialog() {
        val now = Calendar.getInstance() // Get current time

        // Init date picker dialog with current time selected
        val dpd = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )

        // Set min date = current date
        dpd.minDate = now

        // Show date picker dialog
         dpd.show(supportFragmentManager, "Datepickerdialog");
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        // Add leading zero if has
        val daySelected: String = if(dayOfMonth < 10) "0$dayOfMonth" else "" + dayOfMonth
        val monthSelected: String = if(monthOfYear < 10) "0" + (monthOfYear + 1) else "" + dayOfMonth

        // Concatenate to a full date string
        val dateInStringFormat = "$daySelected/$monthSelected/$year"
        binding.viewModel!!.setChosenDate(dateInStringFormat)

//        // Show time picker and stylist picker
//        if(binding.timePickerWrapper.visibility == View.GONE)
//            binding.timePickerWrapper.visibility = View.VISIBLE

        // Setup list of shifts for user to choose after choosing date
        viewModel.setupShiftPickerSpinner(
            this,
            binding.shiftPickerSpinner,
            binding.timePickerLabel,
            binding.timePickerSpinner,
            binding.chooseStylistLabel,
            binding.chooseStylistTextInputLayout
        )
        hideTimePicker()
        hideChooseStylist()
        displayShiftPicker()
    }


    // Back to previous screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun displayEmptyFieldsWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning")
        builder.setMessage("Please fill out all fields!!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun displaySalonChosenRequiredWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Làm ơn chọn một salon!!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun displayChosenDateRequiredWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Vui lòng chọn ngày đặt trước!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun displayStylistBusyWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thông báo")
        builder.setMessage("Khung giờ bạn vừa đặt vừa bị chiếm mất rồi! Vui lòng chọn khung giờ hoặc stylist khác")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }
}