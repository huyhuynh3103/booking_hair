package com.example.hair_booking.ui.admin.service.add_new_service

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminAddNewServiceBinding
import com.example.hair_booking.ui.admin.service.overview.AdminServiceListActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


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
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
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


            var tmpDuration = ""
            if(duration.length > 1) {
                tmpDuration = duration
                while(tmpDuration[0] == '0') {
                    tmpDuration = tmpDuration.substring(1)
                }
            }
            else
                tmpDuration = duration

            var tmpPrice = ""
            if(price.length > 1) {
                tmpPrice = price
                while(tmpPrice[0] == '0') {
                    tmpPrice = tmpPrice.substring(1)
                }
            }
            else
                tmpPrice = price

            if(viewModel.checkEmptyFieldsExist(title, description, tmpDuration, tmpPrice))
                displayEmptyFieldsWarning()
            else {
                if(tmpDuration.toLong() <= 0) {
                    displayInvalidDurationWarning()
                }
                else if(tmpPrice.toLong() <= 0) {
                    displayInvalidPriceWarning()
                }
                else {
                    GlobalScope.launch {
                        val ack = viewModel.saveService(title, description, tmpDuration, tmpPrice)
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

            }
        })
    }

    private fun displayInvalidDurationWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Thời gian dự kiến phải lớn hơn 0 !!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun displayInvalidPriceWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Giá dịch vụ phải lớn hơn 0 !!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
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