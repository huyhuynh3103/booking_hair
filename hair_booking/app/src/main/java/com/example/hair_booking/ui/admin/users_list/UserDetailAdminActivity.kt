package com.example.hair_booking.ui.admin.users_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityUserDetailAdminBinding
import com.example.hair_booking.ui.normal_user.profile.NormalUserProfileViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class UserDetailAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailAdminBinding
    private val viewModel: UserDetailAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail_admin)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val userId = intent.getStringExtra("userId")
        getSelectedNormalUserProfile(userId!!)
        setOnClickListenerForButton()
    }

    private fun getSelectedNormalUserProfile(id: String) {
        binding.viewModel?.getUserDetail(id)
    }

    private fun setOnClickListenerForButton() {
    }
}