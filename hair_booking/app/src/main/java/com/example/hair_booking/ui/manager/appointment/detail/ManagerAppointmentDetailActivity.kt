package com.example.hair_booking.ui.manager.appointment.detail

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerAppointmentDetailBinding
import com.example.hair_booking.services.booking.DateServices
import com.example.hair_booking.ui.manager.appointment.detail.edit.ManagerEditAppointmentActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ManagerAppointmentDetailActivity: AppCompatActivity() {

    private val REQUEST_CODE_EDIT_APPOINTMENT: Int = 1111

    private lateinit var binding: ActivityManagerAppointmentDetailBinding
    private val appointmentSaved: MutableLiveData<HashMap<String, *>?> = MutableLiveData()

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ManagerAppointmentDetailViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get appointment saved
        val appointmentId: String? = intent.getStringExtra("appointmentId")
        if (appointmentId != null) {
            lifecycleScope.launch {
                async {
                    viewModel.prepareAppointmentDetail(appointmentId)
                }.await()

                if(DateServices.isBefore(viewModel.bookingDate.value!!, DateServices.currentDateInString())
                    || viewModel.status.value == Constant.AppointmentStatus.isAbort.uppercase()
                    || viewModel.status.value == Constant.AppointmentStatus.isCheckout.uppercase()) {
                    runOnUiThread {
                        hideAllButtons()
                    }
                }
            }
        }

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_appointment_detail)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        observeOnClickEvent()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun observeOnClickEvent() {
        // Observe edit button onclick event to perform navigation to edit appointment screen
        viewModel.editBtnClicked.observe(this, androidx.lifecycle.Observer {
            moveToEditAppointmentScreen()
        })

        // Observe cancel button onclick event
        viewModel.cancelBtnClicked.observe(this, androidx.lifecycle.Observer {
            displayDeleteAlertDialog()
        })

        // Observe check out button onclick event to perform navigation to check out confirm screen
        viewModel.checkOutBtnClicked.observe(this, androidx.lifecycle.Observer {
            displayCheckOutAlertDialog()
        })
    }

    private fun moveToEditAppointmentScreen() {
        val intent = Intent(this, ManagerEditAppointmentActivity::class.java)

        intent.putExtra("appointmentId", viewModel.appointmentId.value)

        startActivityForResult(intent, REQUEST_CODE_EDIT_APPOINTMENT)
    }

    private fun moveToCheckOutConfirmScreen() {
//        val intent = Intent(this, BookingConfirmActivity::class.java)
//
//        intent.putExtra("appointmentSaved", docSaved)
//
//        startActivityForResult(intent, REQUEST_CODE_BOOKING_CONFIRM)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) { // Handle the data from activity result based on request code
            REQUEST_CODE_EDIT_APPOINTMENT -> {
                // Get new appointment id
                val newAppointmentId: String = data?.getStringExtra("newAppointmentId").orEmpty()

                // Call viewmodel to set new appointment id
                if (newAppointmentId.isNotEmpty())
                    viewModel.setAppointmentId(newAppointmentId)
            }
        }
    }

    private fun hideAllButtons() {
        if(binding.managerEditAppointmentBtn.visibility == View.VISIBLE
            && binding.linearWrapper.visibility == View.VISIBLE) {
            binding.managerEditAppointmentBtn.visibility = View.GONE
            binding.linearWrapper.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun displayDeleteAlertDialog() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Hủy lịch đặt")
        //set message for alert dialog
        builder.setMessage("Bạn có thật sự muốn hủy lịch đặt ?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Có"){dialogInterface, which ->
            lifecycleScope.launch {
                val ack = viewModel.updateAppointmentStatus(Constant.AppointmentStatus.isAbort)
                if(ack == true)
                    runOnUiThread {
                        displayUpdateSuccessDialog("Lịch đặt đã được hủy")
                        hideAllButtons()
                    }
            }
        }
        //performing negative action
        builder.setNegativeButton("Không"){dialogInterface, which ->
            // do nothing
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun displayCheckOutAlertDialog() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Thanh toán")
        //set message for alert dialog
        builder.setMessage("Bạn có thật sự muốn thanh toán ?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Có"){dialogInterface, which ->
            lifecycleScope.launch {
                val ack = viewModel.updateAppointmentStatus(Constant.AppointmentStatus.isCheckout)
                if(ack == true)
                    runOnUiThread {
                        displayUpdateSuccessDialog("Thanh toán thành công")
                        hideAllButtons()
                    }
            }
        }
        //performing negative action
        builder.setNegativeButton("Không"){dialogInterface, which ->
            // do nothing
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun displayUpdateSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Thành công")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(getDrawable(R.drawable.ic_baseline_check_24))

        //performing positive action
        builder.setPositiveButton("Ok"){dialogInterface, which ->
            // do nothing
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // Back to main screen when click back button
    override fun onBackPressed() {
        super.onBackPressed()
        parent.recreate()
    }
}