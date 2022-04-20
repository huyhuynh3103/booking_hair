package com.example.hair_booking.ui.admin.managers_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerDetailAdminBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ManagerDetailAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerDetailAdminBinding
    private val viewModel: ManagerDetailAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_detail_admin)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val managerId = intent.getStringExtra("managerId")
        getSelectedNormalUserProfile(managerId!!)
        setOnClickListenerForButton(managerId)
    }

    private fun getSelectedNormalUserProfile(id: String) {
        binding.viewModel?.getAccountDetail(id)

    }

    private fun setOnClickListenerForButton(id: String) {
        // Observe lock button onclick event
        viewModel.lockBtnClicked.observe(this, androidx.lifecycle.Observer {
            GlobalScope.launch {
                viewModel.updateLockAccount(id)
            }
            finish();
            startActivity(intent);
            //val intent = Intent(this, UsersListActivity::class.java)
            //startActivity(intent)
        })

        // Observe save button onclick event
        viewModel.saveBtnClicked.observe(this, androidx.lifecycle.Observer {
            GlobalScope.launch {
                viewModel.updateManager(binding.sWorkplace.selectedItemPosition, id)
            }
            finish();
            startActivity(intent);
            //val intent = Intent(this, UsersListActivity::class.java)
            //startActivity(intent)
        })
    }
}