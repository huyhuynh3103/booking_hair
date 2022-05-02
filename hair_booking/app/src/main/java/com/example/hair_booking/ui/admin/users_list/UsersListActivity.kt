package com.example.hair_booking.ui.admin.users_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityUsersListBinding
import com.google.firebase.firestore.DocumentReference


class UsersListActivity : AppCompatActivity() {
    val REQUEST_CODE = 1111
    private lateinit var binding: ActivityUsersListBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: UsersListViewModel by viewModels()

    private lateinit var usersListAdapter: UsersListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_users_list)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for salon list recyclerview
        usersListAdapter = UsersListAdapter()
        binding.usersListRV.adapter = usersListAdapter

        // Assign linear layout for salon list recyclerview
        binding.usersListRV.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.usersListRV.addItemDecoration(itemDecoration)

        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set onclick event on item in salon list
        setOnUserItemClickedEvent()

    }

    private fun setOnUserItemClickedEvent() {
        usersListAdapter.onItemClick = {position: Int ->
            // Create an intent to send data to next activity
            val intent = Intent(this, UserDetailAdminActivity::class.java)

            val userId: String = viewModel.userList.value?.get(position)?.id ?: ""
            //val salonLocation: String = viewModel.userList.value?.get(position)?.addressToString() ?: ""

            // send chosen salon id and location to next activity
            intent.putExtra("userId", userId)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}