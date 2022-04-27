package com.example.hair_booking.ui.manager.stylist

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
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

        // enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
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

            lifecycleScope.launch {
                // Edit stylist
                if (binding.task == "Edit") {
                    if (isConflict(binding.cbShiftMorning, "morning", binding.viewModel!!.getShiftRef("Hl0aRPOhZaI02vqMRUdr"))
                        || isConflict(binding.cbShiftAfternoon, "afternoon", binding.viewModel!!.getShiftRef("KaZ0Gj0MzYvkZkpHAs8Q"))
                        || isConflict(binding.cbShiftEvening, "evening", binding.viewModel!!.getShiftRef("cRAgvOR26BYKSRaHbG0g"))) {
                        Toast.makeText(applicationContext,
                            "Thay đổi ca làm việc không thể thực hiện do nhân viên đã có lịch chờ xử lý", Toast.LENGTH_LONG)
                            .show()
                    }
                    else {
                        val stylist = getDataFromUI()
                        binding.viewModel!!.updateStylist(id, stylist)

                        val replyIntent = Intent()
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                }
                // Add stylist
                else {
                    val stylist = getDataFromUI()
                    binding.viewModel!!.addStylist(stylist)

                    val replyIntent = Intent()
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
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

//    private fun isShiftChange(): Boolean {
//        val morningShift = binding.viewModel.stylist.value?.shifts?.get("morning")?.get("isWorking")
//        val afternoonShift = binding.viewModel.stylist.value?.shifts?.get("afternoon")?.get("isWorking")
//        val eveningShift = binding.viewModel.stylist.value?.shifts?.get("evening")?.get("isWorking")
//
//        return !(binding.cbShiftMorning.isChecked == morningShift
//                && binding.cbShiftAfternoon.isChecked == afternoonShift
//                && binding.cbShiftEvening.isChecked == eveningShift)
//    }

    private suspend fun isConflict(checkBox: CheckBox, shift: String, shiftRef: DocumentReference?): Boolean {
        val id = intent.getStringExtra("StylistID").toString()
        val isWorking = binding.viewModel!!.stylist.value?.shifts?.get(shift)?.get("isWorking")

        if (!checkBox.isChecked && isWorking == true) {
            Log.i("isConflict", "Check is $shift booked")
            if (binding.viewModel!!.isBooked(binding.viewModel!!.getStylistRef(id), shiftRef)) return true
        }

        return false
    }

    private suspend fun getDataFromUI(): Stylist {
        val id = intent.getStringExtra("StylistID").toString()
        val name = binding.etStylistName.text.toString()
        val avatar = ""
        val description = binding.tvStylistDescription.text.toString()

        var shift: HashMap<String, HashMap<String, *>>
        var morningShift: HashMap<String, *>
        var afternoonShift: HashMap<String, *>
        var eveningShift: HashMap<String, *>

        // Get selected workplace
        var workplace: DocumentReference? = binding.viewModel!!.getSelectedWorkplace(binding.sWorkplace.selectedItemPosition)

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
        return Stylist(id, name, avatar, description, shift, workplace!!, false)
    }
}