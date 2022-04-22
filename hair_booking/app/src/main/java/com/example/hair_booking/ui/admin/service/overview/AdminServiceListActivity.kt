package com.example.hair_booking.ui.admin.service.overview

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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminServiceListBinding
import com.example.hair_booking.ui.admin.service.add_new_service.AdminAddNewServiceActivity
import com.example.hair_booking.ui.admin.service.edit_service.AdminEditServiceActivity
import com.example.hair_booking.ui.normal_user.booking.booking_confirm.BookingConfirmActivity

class AdminServiceListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminServiceListBinding
    private val REQUEST_CODE_DETAIL: Int = 1111

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: AdminServiceListViewModel by viewModels()

    private lateinit var serviceListAdapter: AdminServiceListAdapter
    @RequiresApi(Build.VERSION_CODES.N)
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Set onclick event on item in appointment list
        setOnServiceItemClickedEvent()

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

    private fun setOnServiceItemClickedEvent() {
//        serviceListAdapter.onItemClick = { position: Int ->
//            val intent = Intent(this, Ad::class.java)
//
//            intent.putExtra("appointmentId", viewModel.appointmentList.value?.get(position)?.id)
//
//            startActivityForResult(intent, REQUEST_CODE_DETAIL)
//        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun observeOnClickEvent() {
        // Observe add button onclick event
        viewModel.addNewServiceBtnClicked.observe(this, androidx.lifecycle.Observer {
            moveToAddNewServiceScreen()
        })

        // Observe edit button onclick event
        viewModel.editServiceBtnClicked.observe(this, androidx.lifecycle.Observer {
            moveToEditServiceScreen()
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

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}