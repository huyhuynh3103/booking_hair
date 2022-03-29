package com.example.hair_booking.ui.normal_user.salon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityNormalUserSalonDetailBinding

class NormalUserSalonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNormalUserSalonDetailBinding
    private val viewModel: NormalUserSalonDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_normal_user_salon_detail)

        // enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // back to previous screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}