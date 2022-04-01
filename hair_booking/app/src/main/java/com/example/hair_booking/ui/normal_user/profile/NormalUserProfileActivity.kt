package com.example.hair_booking.ui.normal_user.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hair_booking.R
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.databinding.ActivityNormalUserProfileBinding

class NormalUserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNormalUserProfileBinding
    private val viewModel: NormalUserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_normal_user_profile)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        getSelectedNormalUserProfile()
        setOnClickListenerForButton()
    }

    private fun getSelectedNormalUserProfile() {
        binding.viewModel?.getSalonDetail("lLed4Jd1HRPzEmwREbkl")
    }

    private fun setOnClickListenerForButton() {
    }
}