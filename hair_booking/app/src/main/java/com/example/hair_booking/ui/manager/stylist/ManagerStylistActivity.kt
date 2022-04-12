package com.example.hair_booking.ui.manager.stylist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityChooseStylistBinding
import com.example.hair_booking.databinding.ActivityManagerStylistDetailBinding

class ManagerStylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseStylistBinding
    private val viewModel: ManagerStylistViewModel by viewModels()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_stylist)
    }
}