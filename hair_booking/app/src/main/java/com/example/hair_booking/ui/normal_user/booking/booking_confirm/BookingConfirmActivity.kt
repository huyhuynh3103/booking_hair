package com.example.hair_booking.ui.normal_user.booking.booking_confirm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityBookingConfirmBinding
import com.example.hair_booking.databinding.ActivityChooseSalonBinding
import com.example.hair_booking.databinding.ActivityChooseStylistBinding
import com.example.hair_booking.ui.admin.service.add_new_service.AdminAddNewServiceActivity
import com.example.hair_booking.ui.normal_user.booking.choose_salon.ChooseSalonViewModel
import com.example.hair_booking.ui.normal_user.booking.choose_salon.SalonListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_stylist.ChooseStylistViewModel
import com.example.hair_booking.ui.normal_user.booking.choose_stylist.ChooseStylistViewModelFactory
import com.example.hair_booking.ui.normal_user.booking.choose_stylist.StylistListAdapter
import com.example.hair_booking.ui.normal_user.home.NormalUserHomeActivity

class BookingConfirmActivity: AppCompatActivity() {

    private lateinit var binding: ActivityBookingConfirmBinding
    private val appointmentSaved: MutableLiveData<HashMap<String, *>?> = MutableLiveData()

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: BookingConfirmViewModel by viewModels{ BookingConfirmViewModelFactory(appointmentSaved) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get appointment saved
        appointmentSaved.value = intent.getSerializableExtra("appointmentSaved") as HashMap<String, *>?


        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_confirm)

//        if(appointmentSaved.value?.get("discountApplied") != null)
//            displayDiscount()


//        if(!appointmentSaved.value?.get("note").toString().isNullOrEmpty())
//            displayNote()

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this



        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.backToMainScreenBtn.setOnClickListener {
            moveToMainScreen()
        }

    }

    private fun moveToMainScreen() {
        val intent = Intent(this, NormalUserHomeActivity::class.java)
        startActivity(intent)
    }

    private fun displayDiscount() {
        if(binding.discountTitle.visibility == View.GONE) {
            binding.discountTitle.visibility = View.VISIBLE
        }
    }

    private fun displayNote() {
        if(binding.note.visibility == View.GONE) {
            binding.note.visibility = View.VISIBLE
        }
    }


    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}