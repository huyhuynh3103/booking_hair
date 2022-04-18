package com.example.hair_booking.ui.normal_user.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.hair_booking.R
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.databinding.ActivityNormalUserProfileBinding
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

        GlobalScope.launch {
            getSelectedNormalUserProfile()
        }
        setOnClickListenerForButton()
    }

    private suspend fun getSelectedNormalUserProfile() {
        binding.viewModel?.getNormalUserDetail("U4mhGl554MTgKbUgMVhA")
        binding.viewModel?.getUserAccountDetail("U4mhGl554MTgKbUgMVhA")
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