package com.example.hair_booking.ui.normal_user.booking

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityBookingBinding
import com.example.hair_booking.ui.normal_user.booking.choose_discount.ChooseDiscountActivity
import com.example.hair_booking.ui.normal_user.booking.choose_salon.ChooseSalonActivity
import com.example.hair_booking.ui.normal_user.booking.choose_service.ChooseServiceActivity
import com.example.hair_booking.ui.normal_user.booking.choose_stylist.ChooseStylistActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*


class BookingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val REQUEST_CODE_CHOOSE_SALON: Int = 1111
    private val REQUEST_CODE_CHOOSE_SERVICE: Int = 2222
    private val REQUEST_CODE_CHOOSE_STYLIST: Int = 3333
    private val REQUEST_CODE_CHOOSE_DISCOUNT: Int = 4444
    private lateinit var binding: ActivityBookingBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: BookingViewModel by viewModels()

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
            else
                moveToChooseStylistScreen()
        })

        // Observe date edit text onclick event to display date picker dialog
        viewModel.dateEditTextClicked.observe(this, androidx.lifecycle.Observer {
            if(viewModel.checkIfSalonEditTextIsEmpty())
                displaySalonChosenRequiredWarning()
            else
                displayDatePickerDialog()
        })

        // Observe time edit text onclick event to display date picker dialog
        viewModel.timeEditTextClicked.observe(this, androidx.lifecycle.Observer {
            if(viewModel.checkIfSalonEditTextIsEmpty())
                displaySalonChosenRequiredWarning()
            else
                displayTimePickerDialog()
        })

        // Observe discount edit text onclick event to perform navigation to choose discount screen
        viewModel.discountEditTextClicked.observe(this, androidx.lifecycle.Observer {
            moveToChooseDiscountScreen()
        })

        // Observe confirm button onclick event
        viewModel.confirmBtnClicked.observe(this, androidx.lifecycle.Observer {
            if(viewModel.checkEmptyFieldsExist())
                displayEmptyFieldsWarning()
            else
                viewModel.saveBookingSchedule()
        })
    }

    private fun moveToChooseSalonScreen() {
        val intent = Intent(this, ChooseSalonActivity::class.java)

        startActivityForResult(intent, REQUEST_CODE_CHOOSE_SALON)
    }

    private fun moveToChooseServiceScreen() {
        val intent = Intent(this, ChooseServiceActivity::class.java)

        startActivityForResult(intent, REQUEST_CODE_CHOOSE_SERVICE)
    }

    private fun moveToChooseStylistScreen() {
        val intent = Intent(this, ChooseStylistActivity::class.java)

        intent.putExtra("chosenSalonId", viewModel.salonId)
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_STYLIST)
    }

    private fun moveToChooseDiscountScreen() {
        val intent = Intent(this, ChooseDiscountActivity::class.java)

        startActivityForResult(intent, REQUEST_CODE_CHOOSE_DISCOUNT)
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
                // Call viewmodel to set chosen service
                if(serviceId.isNotEmpty() && serviceName.isNotEmpty())
                    binding.viewModel!!.setChosenService(serviceId, serviceName)
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
                // Call viewmodel to set chosen discount
                if(discountId.isNotEmpty() && discountTitle.isNotEmpty())
                    binding.viewModel!!.setChosenDiscount(discountId, discountTitle)
            }
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
    }

    private fun displayTimePickerDialog() {
        val now = Calendar.getInstance() // Get current time

        // Init time picker dialog with current time selected
        val tpd = TimePickerDialog.newInstance(
            this,
            now[Calendar.HOUR_OF_DAY],  // Initial hour selection
            now[Calendar.MINUTE],  // Initial minute selection
            true // Set 24hour mode
        )

        // Set min time = current time if the booking date selected is today
//        val isInOpeningHour: Boolean = viewModel.isInOpeningHour(now[Calendar.HOUR_OF_DAY], now[Calendar.MINUTE])
        if(viewModel.isToday(now))
            tpd.setMinTime(now[Calendar.HOUR_OF_DAY], now[Calendar.MINUTE], now[Calendar.SECOND])
//        else if(!isInOpeningHour) {
//            val openHour: Int = viewModel.op
//        }

        // Show time picker dialog
        tpd.show(supportFragmentManager, "Timepickerdialog");
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val timeInStringFormat: String = "" + hourOfDay + "h" + minute
        binding.viewModel!!.setChosenTime(timeInStringFormat)
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



}