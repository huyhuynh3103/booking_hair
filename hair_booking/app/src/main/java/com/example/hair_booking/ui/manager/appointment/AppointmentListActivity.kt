package com.example.hair_booking.ui.manager.appointment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerAppointmentListBinding

class AppointmentListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerAppointmentListBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: AppointmentListViewModel by viewModels()

    private lateinit var appointmentListAdapter: AppointmentListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_appointment_list)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for salon list recyclerview
        appointmentListAdapter = AppointmentListAdapter()
        binding.appointmentListRecyclerView.adapter = appointmentListAdapter

        // Assign linear layout for appointment list recyclerview
        binding.appointmentListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.appointmentListRecyclerView.addItemDecoration(itemDecoration)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupFilterSpinner()

        // Set onclick event on item in appointment list
        setOnAppointmentItemClickedEvent()


    }

    private fun setOnAppointmentItemClickedEvent() {
//        appointmentListAdapter.onItemClick = { position: Int ->
//            // Create an intent to send data back to previous activity
//            val replyIntent = Intent()
//
//            val salonId: String = viewModel.salonList.value?.get(position)?.id ?: ""
//            val salonLocation: String = viewModel.salonList.value?.get(position)?.addressToString() ?: ""
//
//            // send chosen salon id and location back to previous activity
//            replyIntent.putExtra("salonId", salonId)
//            replyIntent.putExtra("salonLocation", salonLocation)
//            setResult(Activity.RESULT_OK, replyIntent)
//            finish()
//        }
    }

    private fun setupFilterSpinner() {
        // Setup class name picker
        val filterSpinner: Spinner = binding.appointmentListFilter


        // Create an adapter to display list of filter options
        var items: ArrayList<String> = arrayListOf("Tất cả", "Chấp nhận", "Chờ duyệt", "Từ chối") // Define placeholder
        val filterSpinnerAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            items
        ) {

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return super.getDropDownView(position, convertView, parent) as TextView
            }
        }

        // Set layout for all rows of spinner
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Assign adapter to the spinner
        filterSpinner.adapter = filterSpinnerAdapter
    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}