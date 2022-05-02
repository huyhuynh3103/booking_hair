package com.example.hair_booking.ui.admin.salon

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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
                    val salon = getDataFromUI()
                    binding.viewModel!!.updateSalon(id, salon)

                    val replyIntent = Intent()
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
                // Add stylist
                else {
                    val salon = getDataFromUI()
                    binding.viewModel!!.addSalon(salon)

                    val replyIntent = Intent()
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
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
        val city  = binding.etSalonAddressCity.text.toString()

        address = hashMapOf("streetNumber" to number, "streetName" to street, "ward" to ward, "district" to district, "city" to city)

        // Data from UI
        if (binding.task == "Edit") {
            return Salon(id!!, name, avatar, description, rate!!, openHour, closeHour, address, phone, false)
        }
        else return Salon(id!!, name, avatar, description, 0.0, openHour, closeHour, address, phone, false)
    }
}