package com.example.hair_booking.ui.normal_user.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.hair_booking.R
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.example.hair_booking.databinding.ActivityNormalUserProfileBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.ui.normal_user.booking.BookingActivity
import com.example.hair_booking.ui.normal_user.history.overview.HistoryBooking
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NormalUserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNormalUserProfileBinding
    private val viewModel: NormalUserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_normal_user_profile)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //val userId = intent.getStringExtra("userId")


        GlobalScope.launch {
            getSelectedNormalUserProfile()
        }
        setOnClickListenerForButton()
    }

    private suspend fun getSelectedNormalUserProfile() {

        binding.viewModel?.getUserAccountDetail("U4mhGl554MTgKbUgMVhA")
        binding.viewModel?.getNormalUserDetail("U4mhGl554MTgKbUgMVhA")
    }

    private fun setOnClickListenerForButton() {
        // Observe lock button onclick event
        viewModel.saveBtnClicked.observe(this, androidx.lifecycle.Observer {
            val selectedGender = binding.rgGender.checkedRadioButtonId
            val radioButton: RadioButton = findViewById(selectedGender)
            GlobalScope.launch {
                viewModel.updateNormalUser(binding.etNameUser.text.toString(), binding.etPhoneUser.text.toString(), radioButton.text.toString(), "U4mhGl554MTgKbUgMVhA")
            }
            finish();
            startActivity(intent);
            //val intent = Intent(this, UsersListActivity::class.java)
            //startActivity(intent)
        })
    }
}