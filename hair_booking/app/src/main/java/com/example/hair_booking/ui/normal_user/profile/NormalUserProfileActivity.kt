package com.example.hair_booking.ui.normal_user.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import com.example.hair_booking.R
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.hair_booking.databinding.ActivityNormalUserProfileBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.GlobalScope
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


        lifecycleScope.launch {
            getSelectedNormalUserProfile()
        }
        setOnClickListenerForButton()
    }

    private suspend fun getSelectedNormalUserProfile() {

        binding.viewModel?.getUserAccountDetail()
        binding.viewModel?.getNormalUserDetail()
    }

    private fun setOnClickListenerForButton() {
        // Observe lock button onclick event
        viewModel.saveBtnClicked.observe(this, androidx.lifecycle.Observer {
            val selectedGender = binding.rgGender.checkedRadioButtonId
            val radioButton: RadioButton = findViewById(selectedGender)
            lifecycleScope.launch {
                // find id for normal user
                val currentUser = AuthRepository.getCurrentUser()
                val email = currentUser!!.email
                val account = dbServices.getAccountServices()!!.getUserAccountByEmail(email!!)
                if(account!=null){
                    val idAccount = account.id
                    val normalUser = dbServices.getNormalUserServices()!!.getUserByAccountId(idAccount)
                    if(normalUser!=null){
                        val idUser = normalUser.id
                        AuthRepository.updateProfile(binding.etNameUser.text.toString())
                        viewModel.updateNormalUser(binding.etNameUser.text.toString(), binding.etPhoneUser.text.toString(), radioButton.text.toString(), idUser)
                        finish()
                        startActivity(intent)
                    }
                    else{
                        Log.d("huy","account existed but normal user didn't exist")
                    }
                }
                else{
                    Log.d("huy","account not existed")
                }
            }

            //val intent = Intent(this, UsersListActivity::class.java)
            //startActivity(intent)
        })
    }
}