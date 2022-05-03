package com.example.hair_booking.ui.manager.profile

import android.app.ActivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerProfileBinding
import com.example.hair_booking.databinding.ActivityNormalUserProfileBinding
import com.example.hair_booking.ui.normal_user.profile.NormalUserProfileViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ManagerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerProfileBinding
    private val viewModel: ManagerProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_profile)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        GlobalScope.launch {
            binding.viewModel?.getUserAccountDetail()
        }
    }

}