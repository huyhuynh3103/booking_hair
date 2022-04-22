package com.example.hair_booking.ui.admin.service.edit_service


import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminEditServiceBinding
import com.example.hair_booking.ui.admin.service.overview.AdminServiceListActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*


class AdminEditServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminEditServiceBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: AdminEditServiceViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_edit_service)

        // Get id of appointment to be edited
        val serviceToBeEditedId: String? = intent.getStringExtra("serviceId")
        if(serviceToBeEditedId != null) {
            lifecycleScope.launch {
                async {
                    viewModel.prepareData(serviceToBeEditedId)
                }.await()

                // Assign view model to binding
                binding.viewModel = viewModel

                // Tell binding to observe the life cycle of this activity
                binding.lifecycleOwner = this@AdminEditServiceActivity




                // Enable back button
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                observeOnClickEvent()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun observeOnClickEvent() {
        // Observe confirm button onclick event
        viewModel.confirmBtnClicked.observe(this, androidx.lifecycle.Observer {
            val title = binding.adminEditServiceTitleEditText.text.toString()
            val description = binding.adminEditServiceDescriptionEditText.text.toString()
            val duration = binding.adminEditServiceDurationEditText.text.toString()
            val price = binding.adminEditServicePriceEditText.text.toString()
            if(viewModel.checkEmptyFieldsExist(title, description, duration, price))
                displayEmptyFieldsWarning()
            else {
                GlobalScope.launch {
                    val ack = viewModel.updateService(title, description, duration, price)
                    if(!ack) {
                        runOnUiThread {
                            displayEditServiceFailed()
                        }
                    }
                    else {
                        runOnUiThread {
                            displayAddSuccessDialog("Chỉnh sửa dịch vụ thành công")
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

    private fun displayEditServiceFailed() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thất bại")
        builder.setMessage("Chỉnh sửa dịch vụ thất bại!!!")

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