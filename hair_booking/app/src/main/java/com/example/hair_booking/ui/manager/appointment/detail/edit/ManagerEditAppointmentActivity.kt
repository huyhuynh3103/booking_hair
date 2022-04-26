package com.example.hair_booking.ui.manager.appointment.detail.edit

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerEditAppointmentBinding
import com.example.hair_booking.services.booking.BookingServices
import com.example.hair_booking.ui.manager.appointment.detail.ManagerAppointmentDetailActivity
import com.example.hair_booking.ui.normal_user.booking.choose_discount.ChooseDiscountActivity
import com.example.hair_booking.ui.normal_user.booking.choose_salon.ChooseSalonActivity
import com.example.hair_booking.ui.normal_user.booking.choose_service.ChooseServiceActivity
import com.example.hair_booking.ui.normal_user.booking.choose_stylist.ChooseStylistActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ManagerEditAppointmentActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener{
    private val REQUEST_CODE_CHOOSE_SALON: Int = 1111
    private val REQUEST_CODE_CHOOSE_SERVICE: Int = 2222
    private val REQUEST_CODE_CHOOSE_STYLIST: Int = 3333
    private val REQUEST_CODE_CHOOSE_DISCOUNT: Int = 4444
    private val REQUEST_CODE_CONFIRM: Int = 5555
    private lateinit var binding: ActivityManagerEditAppointmentBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ManagerEditAppointmentViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_edit_appointment)

        // Get id of appointment to be edited
        val appointmentToBeEditedId: String? = intent.getStringExtra("appointmentId")
        if(appointmentToBeEditedId != null) {
            lifecycleScope.launch {
                async {
                    viewModel.prepareAppointmentDetail(
                        this@ManagerEditAppointmentActivity,
                        appointmentToBeEditedId,
                        binding.managerShiftPickerSpinner,
                        binding.managerTimePickerLabel,
                        binding.managerTimePickerSpinner,
                        binding.managerChooseStylistLabel,
                        binding.managerChooseStylistTextInputLayout
                    )
                }.await()

                // Assign view model to binding
                binding.viewModel = viewModel

                // Tell binding to observe the life cycle of this activity
                binding.lifecycleOwner = this@ManagerEditAppointmentActivity




                // Enable back button
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                observeOnClickEvent()
            }
        }
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
            else if(viewModel.shiftId.value.isNullOrEmpty() || viewModel.bookingTime.value.isNullOrEmpty())
                displayChosenTimeRequiredWarning()
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
                    val docSaved = viewModel.updateBookingSchedule(binding.managerNote.text.toString())
                    if(docSaved.isNullOrEmpty()) {
                        runOnUiThread {
                            displayStylistBusyWarning()
                        }
                    }
                    else {
                        moveToAppointmentDetailScreen()
                    }
                }
            }
        })

    }

    private fun moveToAppointmentDetailScreen() {
        val intent = Intent(this, ManagerAppointmentDetailActivity::class.java)
        intent.putExtra("appointmentId", viewModel.appointmentId.value)

        startActivity(intent)
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

            intent.putExtra("userId", viewModel.userId.value)
            intent.putExtra("chosenDate", viewModel.bookingDate.value)
            intent.putExtra("serviceId", viewModel.serviceId.value)
            intent.putExtra("userCurrentPoint", viewModel.userDiscountPoint.value)
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
                    binding.managerShiftPickerSpinner,
                    binding.managerTimePickerLabel,
                    binding.managerTimePickerSpinner,
                    binding.managerChooseStylistLabel,
                    binding.managerChooseStylistTextInputLayout,
                    false
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
        if(binding.managerDatePickerWrapper.visibility == View.GONE)
            binding.managerDatePickerWrapper.visibility = View.VISIBLE
    }

    private fun displayShiftPicker() {
        if(binding.managerTimePickerWrapper.visibility == View.GONE)
            binding.managerTimePickerWrapper.visibility = View.VISIBLE
    }

    private fun displayTimePicker() {
        if(binding.managerTimePickerLabel.visibility == View.GONE && binding.managerTimePickerSpinner.visibility == View.GONE) {
            binding.managerTimePickerLabel.visibility = View.VISIBLE
            binding.managerTimePickerSpinner.visibility = View.VISIBLE
        }
    }

    private fun hideTimePicker() {
        if(binding.managerTimePickerLabel.visibility == View.VISIBLE && binding.managerTimePickerSpinner.visibility == View.VISIBLE) {
            binding.managerTimePickerLabel.visibility = View.GONE
            binding.managerTimePickerSpinner.visibility = View.GONE
        }
    }

    private fun hideChooseStylist() {
        if(binding.managerChooseStylistLabel.visibility == View.VISIBLE && binding.managerChooseStylistTextInputLayout.visibility == View.VISIBLE) {
            binding.managerChooseStylistLabel.visibility = View.GONE
            binding.managerChooseStylistTextInputLayout.visibility = View.GONE
        }
    }

    private fun hideTotalPrice() {
        if(binding.managerTotalPriceLabel.visibility == View.VISIBLE
            && binding.managerTotalPrice.visibility == View.VISIBLE
            && binding.managerVndUnit.visibility == View.VISIBLE) {
            binding.managerTotalPriceLabel.visibility = View.GONE
            binding.managerTotalPrice.visibility = View.GONE
            binding.managerVndUnit.visibility = View.GONE
        }
    }

    private fun displayTotalPrice() {
        if(binding.managerTotalPriceLabel.visibility == View.GONE
            && binding.managerTotalPrice.visibility == View.GONE
            && binding.managerVndUnit.visibility == View.GONE) {
            binding.managerTotalPriceLabel.visibility = View.VISIBLE
            binding.managerTotalPrice.visibility = View.VISIBLE
            binding.managerVndUnit.visibility = View.VISIBLE
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

        // Setup list of shifts for user to choose after choosing date
        viewModel.setupShiftPickerSpinner(
            this,
            binding.managerShiftPickerSpinner,
            binding.managerTimePickerLabel,
            binding.managerTimePickerSpinner,
            binding.managerChooseStylistLabel,
            binding.managerChooseStylistTextInputLayout,
            false
        )
        hideTimePicker()
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
        builder.setTitle("Cảnh báo")
        builder.setMessage("Làm ơn điền đầy đủ các trường !!!")

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

    private fun displayChosenTimeRequiredWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Vui lòng chọn ca và giờ cắt tóc trước!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }


}