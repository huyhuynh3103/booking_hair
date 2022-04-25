package com.example.hair_booking.ui.normal_user.booking.choose_salon

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityChooseSalonBinding

class ChooseSalonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseSalonBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ChooseSalonViewModel by viewModels()

    private lateinit var salonListAdapter: SalonListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_salon)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for salon list recyclerview
        salonListAdapter = SalonListAdapter()
        binding.salonListRecyclerView.adapter = salonListAdapter

        // Assign linear layout for salon list recyclerview
        binding.salonListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.salonListRecyclerView.addItemDecoration(itemDecoration)

        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set onclick event on item in salon list
        setOnSalonItemClickedEvent()

    }

    private fun setOnSalonItemClickedEvent() {
        salonListAdapter.onItemClick = {position: Int ->
            // Create an intent to send data back to previous activity
            val replyIntent = Intent()

            val salonId: String = viewModel.salonList.value?.get(position)?.id ?: ""
            val salonLocation: String = viewModel.salonList.value?.get(position)?.addressToString() ?: ""

            // send chosen salon id and location back to previous activity
            replyIntent.putExtra("salonId", salonId)
            replyIntent.putExtra("salonLocation", salonLocation)
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