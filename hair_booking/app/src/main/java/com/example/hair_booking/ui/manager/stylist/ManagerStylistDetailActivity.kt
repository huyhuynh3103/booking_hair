package com.example.hair_booking.ui.manager.stylist

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerStylistDetailBinding
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.auth.AuthRepository
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException


class ManagerStylistDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerStylistDetailBinding
    private val viewModel: ManagerStylistDetailViewModel by viewModels()

    private lateinit var adapter: ArrayAdapter<Salon>
    private lateinit var id: String
    private var auth: FirebaseUser? = null



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
            else {
                // Set default avatar in case adding stylist
                val resId = R.drawable.default_avatar
                val defaultAvatarUri = Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(resources.getResourcePackageName(resId))
                    .appendPath(resources.getResourceTypeName(resId))
                    .appendPath(resources.getResourceEntryName(resId))
                    .build()
                binding.ivStylistAvatar.setImageURI(defaultAvatarUri)
                viewModel.setAvatarUri(defaultAvatarUri)
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
                        var stylist = getDataFromUI()
                        editStylist(stylist)
                    }
                }
                // Add stylist
                else {


                    val stylist = getDataFromUI()
                    addStylist(stylist)
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
//            val intent = Intent(Intent.ACTION_GET_CONTENT);
//            intent.type = "image/*";
//            startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            binding.ivStylistAvatar.setImageURI(data.data)
            viewModel.setAvatarUri(data.data!!)
        }
    }

    private fun editStylist(stylist: Stylist) {
        try {
            if(viewModel.avatar.value != null) {
                viewModel.toggleProgressBar() // show progress bar

                //File to upload to cloudinary
                MediaManager.get()
                    .upload(viewModel.avatar.value)
                    .option("folder", Constant.ImagePath.stylist)
                    .option("public_id", id) // use stylist id as public id
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            Log.d("cloudinary", "onStart")
                        }
                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            Log.d("cloudinary", "onProgress")
                        }
                        override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                            Log.d("cloudinary", "onSuccess")
                            stylist.setAvatar(resultData?.get("secure_url") as String)

                            lifecycleScope.launch {
                                async {
                                    binding.viewModel!!.updateStylist(id, stylist)
                                }.await()
                                viewModel.toggleProgressBar() // hide progress bar
                                val replyIntent = Intent()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                        }
                        override fun onError(requestId: String?, error: ErrorInfo) {
                            Log.d("cloudinary", "onError")
                        }
                        override fun onReschedule(requestId: String?, error: ErrorInfo) {
                            Log.d("cloudinary", "onReschedule")
                        }
                    }).startNow(this)
            }
            else {
                viewModel.toggleProgressBar() // show progress bar
                // Update without changing avatar
                lifecycleScope.launch {
                    async {
                        binding.viewModel!!.updateStylist(id, stylist)
                    }.await()
                    viewModel.toggleProgressBar() // hide progress bar
                    val replyIntent = Intent()
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun displayAvatarRequiredWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Vui lòng chọn avatar!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun addStylist(stylist: Stylist) {
        try {
            if(viewModel.avatar.value != null) {
                viewModel.toggleProgressBar() // show progress bar

                //File to upload to cloudinary
                MediaManager.get()
                    .upload(viewModel.avatar.value)
                    .option("folder", Constant.ImagePath.stylist)
                    .option("public_id", id) // use stylist id as public id
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            Log.d("cloudinary", "onStart")
                        }
                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            Log.d("cloudinary", "onProgress")
                        }
                        override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                            Log.d("cloudinary", "onSuccess")
                            stylist.setAvatar(resultData?.get("secure_url") as String)

                            lifecycleScope.launch {
                                async {
                                    binding.viewModel!!.addStylist(stylist)
                                }.await()
                                viewModel.toggleProgressBar() // hide progress bar
                                val replyIntent = Intent()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                        }
                        override fun onError(requestId: String?, error: ErrorInfo) {
                            Log.d("cloudinary", "onError")
                        }
                        override fun onReschedule(requestId: String?, error: ErrorInfo) {
                            Log.d("cloudinary", "onReschedule")
                        }
                    }).startNow(this)
            }
            else
                displayAvatarRequiredWarning()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}