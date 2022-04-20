package com.example.hair_booking.ui.normal_user.history.overview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityHistoryBookingBinding

class HistoryBooking : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBookingBinding
    private val viewModel:HistoryBookingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history_booking)
        binding.viewModel= viewModel
        binding.lifecycleOwner = this

        val adapter = HistoryListAdapter()
        binding.historyRecycleView.adapter = adapter
        binding.historyRecycleView.layoutManager = LinearLayoutManager(this)
        binding.historyRecycleView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        //spinner
        val statusSource = ArrayList<String>()
        statusSource.add(Constant.AppointmentStatus.isPending)
        statusSource.add(Constant.AppointmentStatus.isAbort)
        statusSource.add(Constant.AppointmentStatus.isCheckout)

        binding.filterSpinner.adapter = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,statusSource)
        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("huy","onItemSelected Spinner History")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d("huy","onNothingSelected Spinner History")
            }

        }

        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}