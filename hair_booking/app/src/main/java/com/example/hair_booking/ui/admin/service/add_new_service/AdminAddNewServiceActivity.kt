package com.example.hair_booking.ui.admin.service.add_new_service

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminAddNewServiceBinding
import com.example.hair_booking.databinding.ActivityBookingBinding
import com.example.hair_booking.services.booking.BookingServices
import com.example.hair_booking.ui.admin.service.overview.AdminServiceListActivity
import com.example.hair_booking.ui.normal_user.booking.BookingViewModel
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


class AdminAddNewServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddNewServiceBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: AdminAddNewServiceViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_add_new_service)

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
        // Observe confirm button onclick event
        viewModel.confirmBtnClicked.observe(this, androidx.lifecycle.Observer {
            val title = binding.adminAddServiceTitleEditText.text.toString()
            val description = binding.adminAddServiceDescriptionEditText.text.toString()
            val duration = binding.adminAddServiceDurationEditText.text.toString()
            val price = binding.adminAddServicePriceEditText.text.toString()
            if(viewModel.checkEmptyFieldsExist(title, description, duration, price))
                displayEmptyFieldsWarning()
            else {
                GlobalScope.launch {
                    val ack = viewModel.saveService(title, description, duration, price)
                    if(!ack) {
                        runOnUiThread {
                            displayAddNewServiceFailed()
                        }
                    }
                    else {
                        runOnUiThread {
                            displayAddSuccessDialog("Thêm mới dịch vụ thành công")
                        }

                    }
                }
            }
        })
    }

    private fun moveToServiceListScreen() {
        val intent = Intent(this, AdminServiceListActivity::class.java)
        startActivity(intent)
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
        builder.setMessage("Vui lòng điền đầy đủ các trường!!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun displayAddNewServiceFailed() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thất bại")
        builder.setMessage("Thêm mới dịch vụ thất bại!!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun displayAddSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Thành công")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(getDrawable(R.drawable.ic_baseline_check_24))

        //performing positive action
        builder.setPositiveButton("Ok"){dialogInterface, which ->
            moveToServiceListScreen()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}