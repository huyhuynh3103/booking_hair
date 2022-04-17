package com.example.hair_booking.ui.admin.managers_list

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
import com.example.hair_booking.databinding.ActivityManagersListBinding

class ManagersListActivity : AppCompatActivity() {
    val REQUEST_CODE = 1111
    private lateinit var binding: ActivityManagersListBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ManagersListViewModel by viewModels()

    private lateinit var managersListAdapter: ManagersListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_managers_list)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for salon list recyclerview
        managersListAdapter = ManagersListAdapter()
        binding.managersListRV.adapter = managersListAdapter

        // Assign linear layout for salon list recyclerview
        binding.managersListRV.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.managersListRV.addItemDecoration(itemDecoration)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set onclick event on item in salon list
        setOnUserItemClickedEvent()

    }

    private fun setOnUserItemClickedEvent() {
        managersListAdapter.onItemClick = {position: Int ->
            // Create an intent to send data to next activity
            val intent = Intent(this, ManagerDetailAdminActivity::class.java)

            val managerId: String = viewModel.accountList.value?.get(position)?.id ?: ""
            //val salonLocation: String = viewModel.userList.value?.get(position)?.addressToString() ?: ""

            // send chosen salon id and location to next activity
            intent.putExtra("managerId", managerId)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}