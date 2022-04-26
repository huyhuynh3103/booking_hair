package com.example.hair_booking.ui.manager.appointment.overview

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerAppointmentListBinding
import com.example.hair_booking.ui.manager.appointment.detail.ManagerAppointmentDetailActivity
import com.google.android.material.navigation.NavigationView

class ManagerAppointmentListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerAppointmentListBinding
    private val REQUEST_CODE_DETAIL: Int = 1111

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ManagerAppointmentListViewModel by viewModels()

    private lateinit var appointmentListAdapter: ManagerAppointmentListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_appointment_list)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        setupAppointmentListAdapter()


        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSearchBar()
        setupFilterSpinner()

        // Set onclick event on item in appointment list
        setOnAppointmentItemClickedEvent()


    }

    private fun setupAppointmentListAdapter() {
        // Create adapter for salon list recyclerview
        appointmentListAdapter = ManagerAppointmentListAdapter()
        binding.appointmentListRecyclerView.adapter = appointmentListAdapter

        // Assign linear layout for appointment list recyclerview
        binding.appointmentListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.appointmentListRecyclerView.addItemDecoration(itemDecoration)
    }

    private fun setOnAppointmentItemClickedEvent() {
        appointmentListAdapter.onItemClick = { position: Int ->
            val intent = Intent(this, ManagerAppointmentDetailActivity::class.java)

            intent.putExtra("appointmentId", viewModel.appointmentList.value?.get(position)?.id)

            startActivityForResult(intent, REQUEST_CODE_DETAIL)
        }
    }

    private fun setupFilterSpinner() {
        // Setup class name picker
        val filterSpinner: Spinner = binding.appointmentListFilter


        // Create an adapter to display list of filter options
        var items: ArrayList<String> = arrayListOf(
            "Tất cả",
            Constant.AppointmentStatus.isPending,
            Constant.AppointmentStatus.isCheckout,
            Constant.AppointmentStatus.isAbort)
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

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position  > 0) {
                    val selectedStatus = items[position]
                    // Filter appointments by subId
                    var appointmentToBeHiddenIndex: ArrayList<Int> = ArrayList()
                    viewModel.appointmentList.value!!.forEach { appointment ->
                        if(selectedStatus != appointment.status)
                            appointmentToBeHiddenIndex.add(viewModel.appointmentList.value!!.indexOf(appointment))
                    }


                    appointmentListAdapter.setAppointmentToBeHiddenIndexWhenFilter(appointmentToBeHiddenIndex)
                }
                else
                    appointmentListAdapter.setAppointmentToBeHiddenIndexWhenFilter(null)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun setupSearchBar() {
        var autoCompleteSearchBar: AutoCompleteTextView = binding.appointmentListSearchBar

        // Observe dataset changed
        // Observe date edit text onclick event to display date picker dialog
         viewModel.appointmentSubIds.observe(this, androidx.lifecycle.Observer {

             // Create adapter for auto complete search bar
             var autoCompleteSearchBarAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, it)
             autoCompleteSearchBar.setAdapter(autoCompleteSearchBarAdapter)
             autoCompleteSearchBar.addTextChangedListener(object: TextWatcher {
                 override fun afterTextChanged(s: Editable?) {}

                 override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                 override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                     if(s != "" && s != "\n") {
                         // Filter appointments by subId
                         var appointmentToBeHiddenIndex: ArrayList<Int> = ArrayList()

                         viewModel.appointmentList.value!!.forEach { appointment ->
                             val keyword = s.toString()
                             Log.d("text1231", keyword)
                             if(!appointment.appointmentSubId!!.contains(keyword))
                                 appointmentToBeHiddenIndex.add(viewModel.appointmentList.value!!.indexOf(appointment))
                         }

                         appointmentListAdapter.setAppointmentToBeHiddenIndexWhenSearch(appointmentToBeHiddenIndex)
                     }
                     else
                         appointmentListAdapter.setAppointmentToBeHiddenIndexWhenSearch(null)

                 }
             })
        })


    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("xk123", "aaa")
        finish()
        return true
    }
}