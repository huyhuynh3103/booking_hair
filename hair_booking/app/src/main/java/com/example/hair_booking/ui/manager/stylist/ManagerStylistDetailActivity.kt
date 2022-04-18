package com.example.hair_booking.ui.manager.stylist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerStylistDetailBinding
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ManagerStylistDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerStylistDetailBinding
    private val viewModel: ManagerStylistDetailViewModel by viewModels()

    private lateinit var adapter: ArrayAdapter<Salon>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_stylist_detail)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter = ArrayAdapter<Salon>(this, R.layout.support_simple_spinner_dropdown_item)
        binding.sWorkplace.adapter = adapter

        setOnClickListenerForButton()

        // Support Menu Action
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load data
        binding.task = intent.getStringExtra("Task")
        if (binding.task == "Edit") {
            lifecycleScope.launch {
                // get selected ID from previous activity
                binding.viewModel?.getStylistDetail(intent.getStringExtra("StylistID").toString())
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun setOnClickListenerForButton() {
        binding.bSaveStylistInfo.setOnClickListener() {
            val id = intent.getStringExtra("StylistID").toString()
            val name = binding.etStylistName.text.toString()
            val avatar = ""
            val description = binding.tvStylistDescription.text.toString()

            var workplace: DocumentReference?
            var shift: HashMap<String, HashMap<String, *>>
            var morningShift: HashMap<String, *>
            var afternoonShift: HashMap<String, *>
            var eveningShift: HashMap<String, *>

            lifecycleScope.launch {
                // Get selected workplace
                workplace = binding.viewModel!!.getSelectedWorkplace(binding.sWorkplace.selectedItemPosition)

                // Get checked shift ref
                val morningRef = binding.viewModel!!.getShiftRef("Hl0aRPOhZaI02vqMRUdr")
                val afternoonRef = binding.viewModel!!.getShiftRef("KaZ0Gj0MzYvkZkpHAs8Q")
                val eveningRef = binding.viewModel!!.getShiftRef("cRAgvOR26BYKSRaHbG0g")

                // Morning shift
                if (binding.cbShiftMorning.isChecked) {
                    morningShift = hashMapOf("id" to morningRef, "isWorking" to true)
                }
                else {
                    morningShift = hashMapOf("id" to morningRef, "isWorking" to false)
                }

                // Afternoon shift
                if (binding.cbShiftAfternoon.isChecked) {
                    afternoonShift = hashMapOf("id" to afternoonRef, "isWorking" to true)
                }
                else {
                    afternoonShift = hashMapOf("id" to afternoonRef, "isWorking" to false)
                }

                // Evening shift
                if (binding.cbShiftEvening.isChecked) {
                    eveningShift = hashMapOf("id" to eveningRef, "isWorking" to true)
                }
                else {
                    eveningShift = hashMapOf("id" to eveningRef, "isWorking" to false)
                }

                // Submit to shift hashmap
                shift = hashMapOf("morning" to morningShift, "afternoon" to afternoonShift, "evening" to eveningShift)

                // Data from UI
                val stylist = Stylist(id, name, avatar, description, shift, workplace!!, false)

                if (binding.task == "Edit") {
                    binding.viewModel!!.updateStylist(id, stylist)
                } else {
                    binding.viewModel!!.addStylist(stylist)
                }

                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }

        binding.bDeleteStylist.setOnClickListener() {
            val id = intent.getStringExtra("StylistID").toString()

            lifecycleScope.launch {
                binding.viewModel!!.deleteStylist(id)

                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }
    }
}