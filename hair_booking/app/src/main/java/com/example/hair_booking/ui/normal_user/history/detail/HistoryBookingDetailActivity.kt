package com.example.hair_booking.ui.normal_user.history.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityHistoryBookingDetailBinding
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch


class HistoryBookingDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistoryBookingDetailBinding
    private var id = MutableLiveData<String>()
    private val viewModel: HistoryBookingDetailViewModel by viewModels{HistoryBookingDetailViewModelFactory(id)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_history_booking_detail)
        // get string binding
        id.value = intent.getStringExtra("id")
        binding.viewModel= viewModel
        binding.lifecycleOwner = this@HistoryBookingDetailActivity
        //init
        binding.isCheckOut = Constant.AppointmentStatus.isCheckout
        binding.isPending = Constant.AppointmentStatus.isPending
        binding.isAbort = Constant.AppointmentStatus.isAbort

        // enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // generate QR code
        try{
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(id.value!!, BarcodeFormat.QR_CODE,600,600)
            binding.QRCodeForAppointment.setImageBitmap(bitmap)
        }catch (e:Exception){
            Log.e("history-detai","Qr code exception",e)
        }


        binding.historyContactBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" +binding.historySalonPhoneNumber.text.toString())
            startActivity(dialIntent)
        }
        binding.historyCancelAppointmentBtn.setOnClickListener {
            try{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Xác nhận hủy lịch")
                builder.setMessage("Bạn có chắn chắn hủy lịch có mã: ${binding.historySubId.text}")
                builder.setPositiveButton("Xác nhận") { dialog, which ->
                    viewModel.viewModelScope.launch {
                        dbServices.getAppointmentServices()!!.cancelById(id.value)
                        dialog.dismiss()
                        binding.progressBarHistoryDetail.visibility = View.VISIBLE
                        viewModel.prepareData()
                        binding.progressBarHistoryDetail.visibility = View.INVISIBLE
                    }


                }

                builder.setNegativeButton("Quay lại") { dialog, which ->
                    dialog.cancel()
                }
                builder.show()

            }catch (e:Exception){
                Log.e("huy_exception",e.message.toString(),e)
            }
        }
        binding.historyRatingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, newRating, _ ->
                viewModel.rating(newRating)
            }
        binding.sendRatingBtn.setOnClickListener { sendBtn ->
            val rating = binding.historyRatingBar.rating
            viewModel.viewModelScope.launch {
                try {
                    val appointment = dbServices.getAppointmentServices()!!.getAppointmentById(id.value!!)
                    if(appointment!=null){

                        val hairSalonRef = appointment.hairSalon?.get("id") as DocumentReference?
                        val hairSalonId = hairSalonRef?.id
                        if(hairSalonId!=null){
                            binding.progressBarHistoryDetail.visibility = View.VISIBLE
                            dbServices.getSalonServices()!!.setSalonRatingById(hairSalonId,rating)
                            dbServices.getAppointmentServices()!!.rateAppointment(id.value!!,rating)
                            binding.progressBarHistoryDetail.visibility = View.GONE
                            sendBtn.visibility = View.GONE
                            viewModel.setIndicator(true)
                        }
                    }
                }
                catch (e:Exception){
                    Log.e("sendRatingBtn",e.message,e)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        parent.recreate()
    }
}