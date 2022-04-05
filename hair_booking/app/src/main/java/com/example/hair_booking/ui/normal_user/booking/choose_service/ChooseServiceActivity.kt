package com.example.hair_booking.ui.normal_user.booking.choose_service

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityChooseServiceBinding

class ChooseServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseServiceBinding
    private lateinit var serviceListAdapter: ServiceListAdapter

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ChooseServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_service)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for service list recyclerview
        serviceListAdapter = ServiceListAdapter()
        binding.serviceListRecyclerView.adapter = serviceListAdapter

        // Assign linear layout for salon list recyclerview
        binding.serviceListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.serviceListRecyclerView.addItemDecoration(itemDecoration)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set onclick event on item in service list
        setOnServiceItemClickedEvent()

    }

    private fun setOnServiceItemClickedEvent() {
        serviceListAdapter.onItemClick = {position: Int ->
            // Create an intent to send data back to previous activity
            val replyIntent = Intent()

            val serviceId: String = viewModel.serviceList.value?.get(position)?.id ?: ""
            val serviceName: String = viewModel.serviceList.value?.get(position)?.title ?: ""

            // send chosen service id and name back to previous activity
            replyIntent.putExtra("serviceId", serviceId)
            replyIntent.putExtra("serviceName", serviceName)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }
    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}