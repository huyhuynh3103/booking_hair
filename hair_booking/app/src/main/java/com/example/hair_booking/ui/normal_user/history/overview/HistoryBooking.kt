package com.example.hair_booking.ui.normal_user.history.overview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityHistoryBookingBinding
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.ui.normal_user.history.detail.HistoryBookingDetailActivity
import kotlinx.coroutines.launch

class HistoryBooking : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBookingBinding
    private val viewModel:HistoryBookingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history_booking)
        binding.viewModel= viewModel
        binding.lifecycleOwner = this

        val adapter = HistoryListAdapter()
        binding.historyRecycleView.adapter = adapter
        binding.historyRecycleView.layoutManager = LinearLayoutManager(this)
        binding.historyRecycleView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.onItemClick = { id ->
            val intent = Intent(this,HistoryBookingDetailActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }

        adapter.onCancleClick = { position,subId ->
            if (subId!=null){

                try{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Xác nhận hủy lịch")
                    builder.setMessage("Bạn có chắn chắn hủy lịch có mã: $subId")
                    builder.setPositiveButton("Xác nhận") { dialog, which ->
                        viewModel.viewModelScope.launch {
                            dbServices.getAppointmentServices()!!.cancel(subId)

                            viewModel.cancelAppointment(adapter,position,Constant.AppointmentStatus.isPending)

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
            else
            {
                Toast.makeText(this,"Không thể hủy lịch vì subId null, Thử lại sau",Toast.LENGTH_LONG).show()
            }
        }


        //spinner
        val statusSource = ArrayList<String>()
        statusSource.add(Constant.AppointmentStatus.isPending)
        statusSource.add(Constant.AppointmentStatus.isAbort)
        statusSource.add(Constant.AppointmentStatus.isCheckout)

        binding.filterSpinner.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,statusSource)
        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val statusAppointment = statusSource[pos]
                viewModel.viewModelScope.launch {
                    viewModel.getHistoryList(statusAppointment)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("huy","onNothingSelected Spinner History")
            }

        }

        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}