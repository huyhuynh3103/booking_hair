package com.example.hair_booking.ui.normal_user.booking

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityBookingBinding
import com.example.hair_booking.databinding.ActivityChooseStylistBinding

class ChooseStylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseStylistBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ChooseStylistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_stylist)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for stylist recyclerview
        binding.stylistRecyclerView.adapter = StylistListAdapter()

        // Assign linear layout for stylist recyclerview
        binding.stylistRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.stylistRecyclerView.addItemDecoration(itemDecoration)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}