package com.example.hair_booking.ui.normal_user.history.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityHistoryBookingDetailBinding

class HistoryBookingDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistoryBookingDetailBinding
    private var id = MutableLiveData<String>()
    private val viewModel: HistoryBookingDetailViewModel by viewModels{HistoryBookingDetailViewModelFactory(id)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_history_booking_detail)
        // get string binding
        id.value = intent.getStringExtra("id")
        binding.viewModel= viewModel
        binding.lifecycleOwner = this@HistoryBookingDetailActivity


    }
}