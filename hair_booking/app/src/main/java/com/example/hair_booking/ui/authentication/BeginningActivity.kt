package com.example.hair_booking.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityBeginingBinding

class BeginningActivity : AppCompatActivity() {
    lateinit var binding : ActivityBeginingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_begining)
        binding.getStartedBtn.setOnClickListener {
            val intent = Intent(this,LogInActivity::class.java)
            startActivity(intent)
        }
    }
}