package com.example.hair_booking.ui.normal_user.salon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityNormalUserSalonDetailBinding

class NormalUserSalonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNormalUserSalonDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_user_salon_detail)
    }
}