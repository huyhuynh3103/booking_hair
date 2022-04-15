package com.example.hair_booking.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private val viewModel:SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@SignUpActivity
    }
}