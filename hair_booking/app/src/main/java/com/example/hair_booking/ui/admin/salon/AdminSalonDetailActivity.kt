package com.example.hair_booking.ui.admin.salon

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminSalonDetailBinding
import com.example.hair_booking.model.Salon
import kotlinx.coroutines.launch

class AdminSalonDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminSalonDetailBinding
    private val viewModel: AdminSalonDetailViewModel by viewModels()

    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_salon_detail)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setOnClickListenerForButton()

        // enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get data
        binding.task = intent.getStringExtra("Task")
        id = intent.getStringExtra("SalonID").toString()

        lifecycleScope.launch {
            if (binding.task == "Edit") {
                // get selected ID from previous activity
                binding.viewModel!!.getSalonDetail(id!!)
            }
        }
    }

    // back to previous screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun setOnClickListenerForButton() {
        binding.bSaveSalonInfo.setOnClickListener() {
            lifecycleScope.launch {
                // Edit stylist
                if (binding.task == "Edit") {
                    // Check number validation
                    if (!isValid(binding.etSalonOpenHour.text.toString()) || !isValid(binding.etSalonCloseHour.text.toString())) {
                        Toast.makeText(applicationContext,
                            "Giờ mở cửa hoặc giờ đóng cửa không hợp lệ", Toast.LENGTH_LONG)
                            .show()
                    }
                    // Check logic validation
                    else if (binding.etSalonOpenHour.text.toString().toFloat()
                            .compareTo(binding.etSalonCloseHour.text.toString().toFloat()) >= 0) {
                        Toast.makeText(applicationContext,
                            "Giờ mở cửa không thể lớn hơn hoặc bằng giờ đóng cửa", Toast.LENGTH_LONG)
                            .show()
                    }
                    // Allow to edit
                    else {
                        val salon = getDataFromUI()
                        binding.viewModel!!.updateSalon(id, salon)

                        val replyIntent = Intent()
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                }
                // Add stylist
                else {
                    // Check number validation
                    if (!isValid(binding.etSalonOpenHour.text.toString()) || !isValid(binding.etSalonCloseHour.text.toString())) {
                        Toast.makeText(applicationContext,
                            "Giờ mở cửa hoặc giờ đóng cửa không hợp lệ", Toast.LENGTH_LONG)
                            .show()
                    }
                    // Check logic validation
                    else if (binding.etSalonOpenHour.text.toString().toFloat()
                            .compareTo(binding.etSalonCloseHour.text.toString().toFloat()) >= 0) {
                        Toast.makeText(applicationContext,
                            "Giờ mở cửa không thể lớn hơn hoặc bằng giờ đóng cửa", Toast.LENGTH_LONG)
                            .show()
                    }
                    // Allow to add
                    else {
                        val salon = getDataFromUI()
                        binding.viewModel!!.addSalon(salon)

                        val replyIntent = Intent()
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                }
            }
        }

        binding.bDeleteSalon.setOnClickListener() {
            lifecycleScope.launch {
                binding.viewModel!!.deleteSalon(id)

                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }
    }

    private fun isValid(string: String): Boolean {
        return string.toFloatOrNull() != null && string.toFloat().compareTo(0.0) >= 0 && string.toFloat().compareTo(23.60) < 0
    }

    private fun getDataFromUI(): Salon {
        val name = binding.etSalonName.text.toString()
        val avatar = ""
        val description = binding.etSalonDescription.text.toString()
        val rate = binding.viewModel!!.salon.value?.rate
        val openHour = binding.etSalonOpenHour.text.toString()
        val closeHour = binding.etSalonCloseHour.text.toString()
        var address: HashMap<String, String>
        val phone = binding.etSalonPhone.text.toString()

        val number = binding.etSalonAddressNumber.text.toString()
        val street = binding.etSalonAddressStreet.text.toString()
        val ward = binding.etSalonAddressWard.text.toString()
        val district = binding.etSalonAddressDistrict.text.toString()
        val city = binding.etSalonAddressCity.text.toString()

        address = hashMapOf(
            "streetNumber" to number,
            "streetName" to street,
            "ward" to ward,
            "district" to district,
            "city" to city
        )

        // Data from UI
        return if (binding.task == "Edit") {
            Salon(id!!, name, avatar, description, rate!!, openHour, closeHour, address, phone, false)
        }
        else Salon(id!!, name, avatar, description, 0.0, openHour, closeHour, address, phone, false)
    }
}