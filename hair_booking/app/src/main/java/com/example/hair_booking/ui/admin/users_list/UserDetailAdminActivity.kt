package com.example.hair_booking.ui.admin.users_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityUserDetailAdminBinding
import com.example.hair_booking.ui.normal_user.profile.NormalUserProfileViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UserDetailAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailAdminBinding
    private val viewModel: UserDetailAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail_admin)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val userId = intent.getStringExtra("userId")

        if (userId != null) {
            GlobalScope.launch {
                getSelectedNormalUserProfile(userId)
            }
            setOnClickListenerForButton(userId)
        }
    }

    private suspend fun getSelectedNormalUserProfile(id: String) {
        binding.viewModel?.getUserAccountDetail(id)
        binding.viewModel?.getUserDetail(id)

    }

    private fun setOnClickListenerForButton(id: String) {
        // Observe lock button onclick event
        viewModel.lockBtnClicked.observe(this, androidx.lifecycle.Observer {
            GlobalScope.launch {
                viewModel.updateLockAccount(binding.etStatus.text.toString(), id)
            }
            finish();
            startActivity(intent)
        })
    }
}