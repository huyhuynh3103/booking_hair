package com.example.hair_booking.ui.manager.stylist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerStylistDetailBinding
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class ManagerStylistDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerStylistDetailBinding
    private val viewModel: ManagerStylistDetailViewModel by viewModels()

    private lateinit var adapter: ArrayAdapter<Salon>
    private lateinit var id: String
    private var auth: FirebaseUser? = null

    private val PHOTO_PICKER_REQUEST_CODE = 1111


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val config: HashMap<String, String> = hashMapOf(
                "cloud_name" to "dq4xoyzib",
                "api_key" to "326314381516441",
                "api_secret" to "8L4vO2T2GblSt9wsP__wMjewYBg"
            )
            MediaManager.init(this, config)
        }
        catch (e: Exception) {
            Log.d("cloudinary", e.toString())
        }


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
        id = intent.getStringExtra("StylistID").toString()

        // Get account ID
        auth = AuthRepository.getCurrentUser()

        lifecycleScope.launch {
            binding.viewModel?.getManagerAccount(auth?.email!!)

            if (binding.task == "Edit") {
                // get selected ID from previous activity
                binding.viewModel?.getStylistDetail(this@ManagerStylistDetailActivity, binding.ivStylistAvatar, id)
            }
        }

        observeChangeAvatarBtnOnClickEvent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun setOnClickListenerForButton() {
        binding.bSaveStylistInfo.setOnClickListener() {
            lifecycleScope.launch {
                // Edit stylist
                if (binding.task == "Edit") {
                    // Check workplace conflict
                    if (isWorkplaceConflict()) {
                        Toast.makeText(applicationContext,
                            "Thay đổi nơi làm việc không thể thực hiện do nhân viên đã có lịch chờ xử lý", Toast.LENGTH_LONG)
                            .show()
                    }
                    // Check shifts conflict
                    else if (isShiftConflict(binding.cbShiftMorning, "morning", binding.viewModel!!.getShiftRef("Hl0aRPOhZaI02vqMRUdr"))
                            || isShiftConflict(binding.cbShiftAfternoon, "afternoon", binding.viewModel!!.getShiftRef("KaZ0Gj0MzYvkZkpHAs8Q"))
                            || isShiftConflict(binding.cbShiftEvening, "evening", binding.viewModel!!.getShiftRef("cRAgvOR26BYKSRaHbG0g"))) {
                        Toast.makeText(applicationContext,
                            "Thay đổi ca làm việc không thể thực hiện do nhân viên đã có lịch chờ xử lý", Toast.LENGTH_LONG)
                            .show()
                    }
                    // Allow to edit
                    else {
                        val stylist = getDataFromUI()
                        binding.viewModel!!.updateStylist(this@ManagerStylistDetailActivity, id, stylist)

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
            lifecycleScope.launch {
                // Check booked appointment
                if (binding.viewModel!!.isBooked(binding.viewModel!!.getStylistRef(id))) {
                    Toast.makeText(applicationContext,
                        "Xóa không thể thực hiện do nhân viên đã có lịch chờ xử lý", Toast.LENGTH_LONG)
                        .show()
                }
                // Allow to delete
                else {
                    binding.viewModel!!.deleteStylist(id)

                    val replyIntent = Intent()
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
            }
        }
    }

    private suspend fun isWorkplaceConflict(): Boolean {
        val current = binding.viewModel!!.stylist.value?.workPlace?.id
        val selected = binding.viewModel!!.getSelectedWorkplace(binding.sWorkplace.selectedItemPosition)?.id

        if (current != selected) {
            Log.i("isConflict", "Check workplace")
            if (binding.viewModel!!.isBooked(binding.viewModel!!.getStylistRef(id))) return true
        }

        return false
    }

    private suspend fun isShiftConflict(checkBox: CheckBox, shift: String, shiftRef: DocumentReference?): Boolean {
        val isWorking = binding.viewModel!!.stylist.value?.shifts?.get(shift)?.get("isWorking")

        if (!checkBox.isChecked && isWorking == true) {
            Log.i("isConflict", "Check if $shift shift is booked")
            if (binding.viewModel!!.isBooked(binding.viewModel!!.getStylistRef(id), shiftRef)) return true
        }

        return false
    }

    private suspend fun getDataFromUI(): Stylist {
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

    private fun observeChangeAvatarBtnOnClickEvent() {
        viewModel.changeAvatarBtnClicked.observe(this, androidx.lifecycle.Observer {
            val intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.type = "image/*";
            startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            binding.ivStylistAvatar.setImageURI(data.data)
            viewModel.setAvatarUri(data.data!!)
        }
    }
}