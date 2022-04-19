package com.example.hair_booking.ui.manager.appointment.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityBookingConfirmBinding
import com.example.hair_booking.databinding.ActivityManagerAppointmentDetailBinding
import com.example.hair_booking.ui.normal_user.booking.booking_confirm.BookingConfirmViewModel
import com.example.hair_booking.ui.normal_user.booking.booking_confirm.BookingConfirmViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ManagerAppointmentDetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityManagerAppointmentDetailBinding
    private val appointmentSaved: MutableLiveData<HashMap<String, *>?> = MutableLiveData()

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ManagerAppointmentDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get appointment saved
        val appointmentId: String? = intent.getStringExtra("appointmentId")
        if (appointmentId != null) {
            viewModel.setAppointmentId(appointmentId)
            GlobalScope.launch {
                viewModel.prepareAppointmentDetail()
            }
        }

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_appointment_detail)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}