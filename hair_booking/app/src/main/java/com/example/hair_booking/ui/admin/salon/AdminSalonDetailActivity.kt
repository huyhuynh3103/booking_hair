package com.example.hair_booking.ui.admin.salon

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminSalonDetailBinding
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class AdminSalonDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminSalonDetailBinding
    private val viewModel: AdminSalonDetailViewModel by viewModels()

    private lateinit var id: String

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
                binding.viewModel!!.getSalonDetail(this@AdminSalonDetailActivity, binding.ivSalonAvatar, id!!)
            }
            else {
                // Set default avatar in case adding stylist
                val resId = R.drawable.default_salon
                val defaultAvatarUri = Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(resources.getResourcePackageName(resId))
                    .appendPath(resources.getResourceTypeName(resId))
                    .appendPath(resources.getResourceEntryName(resId))
                    .build()
                binding.ivSalonAvatar.setImageURI(defaultAvatarUri)
                viewModel.setAvatarUri(defaultAvatarUri)
            }
        }

        observeChangeAvatarBtnOnClickEvent()
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
                    editSalon(salon)
                }
                // Add stylist
                else {
                    val salon = getDataFromUI()
                    addSalon(salon)
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

    private fun editSalon(salon: Salon) {
        try {
            if(viewModel.avatar.value != null) {
                viewModel.toggleProgressBar() // show progress bar

                //File to upload to cloudinary
                MediaManager.get()
                    .upload(viewModel.avatar.value)
                    .option("folder", Constant.ImagePath.salon)
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
                            salon.setAvatar(resultData?.get("secure_url") as String)

                            lifecycleScope.launch {
                                async {
                                    binding.viewModel!!.updateSalon(id, salon)
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
                        binding.viewModel!!.updateSalon(id, salon)
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

    private fun addSalon(salon: Salon) {
        try {
            if(viewModel.avatar.value != null) {
                viewModel.toggleProgressBar() // show progress bar

                //File to upload to cloudinary
                MediaManager.get()
                    .upload(viewModel.avatar.value)
                    .option("folder", Constant.ImagePath.salon)
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
                            salon.setAvatar(resultData?.get("secure_url") as String)

                            lifecycleScope.launch {
                                async {
                                    binding.viewModel!!.addSalon(salon)
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
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun observeChangeAvatarBtnOnClickEvent() {
        viewModel.changeAvatarBtnClicked.observe(this, androidx.lifecycle.Observer {
            ImagePicker.with(this)
                .crop() //Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(binding.ivSalonAvatar.width, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
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

            binding.ivSalonAvatar.setImageURI(data.data)
            viewModel.setAvatarUri(data.data!!)
        }
    }
}