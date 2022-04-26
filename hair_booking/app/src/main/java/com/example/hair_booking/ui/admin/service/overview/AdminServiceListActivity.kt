package com.example.hair_booking.ui.admin.service.overview

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminServiceListBinding
import com.example.hair_booking.ui.admin.service.add_new_service.AdminAddNewServiceActivity
import com.example.hair_booking.ui.admin.service.edit_service.AdminEditServiceActivity
import com.example.hair_booking.ui.normal_user.booking.booking_confirm.BookingConfirmActivity
import kotlinx.coroutines.launch

class AdminServiceListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminServiceListBinding
    private val REQUEST_CODE_DETAIL: Int = 1111

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: AdminServiceListViewModel by viewModels()

    private lateinit var serviceListAdapter: AdminServiceListAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_service_list)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        setupServiceListAdapter()


        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        observeOnClickEvent()
    }

    private fun setupServiceListAdapter() {
        // Create adapter for salon list recyclerview
        serviceListAdapter = AdminServiceListAdapter(viewModel)
        binding.adminServiceListRecyclerView.adapter = serviceListAdapter

        // Assign linear layout for appointment list recyclerview
        binding.adminServiceListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.adminServiceListRecyclerView.addItemDecoration(itemDecoration)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeOnClickEvent() {
        // Observe add button onclick event
        viewModel.addNewServiceBtnClicked.observe(this, androidx.lifecycle.Observer {
            moveToAddNewServiceScreen()
        })

        // Observe edit button onclick event
        viewModel.editServiceBtnClicked.observe(this, androidx.lifecycle.Observer {
            moveToEditServiceScreen()
        })

        // Observe delete button onclick event
        viewModel.deleteServiceBtnClicked.observe(this, androidx.lifecycle.Observer {
            displayDeleteAlertDialog()
        })
    }

    private fun moveToAddNewServiceScreen() {
        val intent = Intent(this, AdminAddNewServiceActivity::class.java)
        startActivity(intent)
    }

    private fun moveToEditServiceScreen() {
        val intent = Intent(this, AdminEditServiceActivity::class.java)

        intent.putExtra("serviceId", viewModel.serviceToBeEditedId.value!!)

        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDeleteAlertDialog() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Xóa dịch vụ")
        //set message for alert dialog
        builder.setMessage("Bạn có thật sự muốn xóa dịch vụ này không ?")
        builder.setIcon(android.R.drawable.ic_delete)

        //performing positive action
        builder.setPositiveButton("Có"){dialogInterface, which ->
            lifecycleScope.launch {
                val ack = viewModel.deleteService()
                if(ack) {
                    runOnUiThread {
                        displayUpdateSuccessDialog("Xóa dịch vụ thành công")
                    }
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
//            binding.adminServiceListRecyclerView.adapter?.notifyDataSetChanged()
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}