package com.example.hair_booking.ui.normal_user.booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityBookingBinding
import java.util.*

class BookingActivity : AppCompatActivity() {
    private val REQUEST_CODE_CHOOSE_SALON: Int = 1111
    private lateinit var binding: ActivityBookingBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Observe salon edit text onclick event to perform navigation to choose salon screen
        viewModel.salonEditTextClicked.observe(this, androidx.lifecycle.Observer {
            moveToChooseSalonScreen()
        })

    }

    private fun moveToChooseSalonScreen() {
        val intent = Intent(this, ChooseSalonActivity::class.java)

        startActivityForResult(intent, REQUEST_CODE_CHOOSE_SALON)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_CHOOSE_SALON -> {
                // Get chosen salon location returned from choose salon activity
                val salonLocation: String = data?.getStringExtra("salonLocation").orEmpty()

                // Call viewmodel to set chosen salon location
                if(salonLocation.isNotEmpty())
                    binding.viewModel!!.setSalonLocation(salonLocation)
            }
        }
    }

    // Back to previous screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}