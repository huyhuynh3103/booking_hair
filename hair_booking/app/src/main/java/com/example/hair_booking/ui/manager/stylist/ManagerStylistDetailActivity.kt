package com.example.hair_booking.ui.manager.stylist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerStylistDetailBinding
import com.example.hair_booking.ui.normal_user.booking.BookingActivity

class ManagerStylistDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerStylistDetailBinding
    private val viewModel: ManagerStylistDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_stylist_detail)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        getSelectedStylistDetail()
        setOnClickListenerForButton()

        // enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun getSelectedStylistDetail() {
        binding.viewModel?.getStylistDetail("eD5l1uEdyqZafG0Gcuc3")
    }

    private fun setOnClickListenerForButton() {
        binding.bSaveStylistInfo.setOnClickListener() {
            // save and switch to stylist list screen
        }

        binding.bDeleteStylist.setOnClickListener() {
            // delete and switch to stylist list screen
        }
    }
}