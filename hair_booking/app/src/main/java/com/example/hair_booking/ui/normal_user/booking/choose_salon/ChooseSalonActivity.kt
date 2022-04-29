package com.example.hair_booking.ui.normal_user.booking.choose_salon

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityChooseSalonBinding

class ChooseSalonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseSalonBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ChooseSalonViewModel by viewModels()

    private lateinit var salonListAdapter: SalonListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_salon)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for salon list recyclerview
        salonListAdapter = SalonListAdapter()
        binding.salonListRecyclerView.adapter = salonListAdapter

        // Assign linear layout for salon list recyclerview
        binding.salonListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.salonListRecyclerView.addItemDecoration(itemDecoration)

        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        setupSalonCityFilterSpinner()
//        setupSalonDistrictFilterSpinner()
        // Set onclick event on item in salon list
        setOnSalonItemClickedEvent()

    }

    private fun setOnSalonItemClickedEvent() {
        salonListAdapter.onItemClick = {position: Int ->
            // Create an intent to send data back to previous activity
            val replyIntent = Intent()

            val salonId: String = viewModel.salonList.value?.get(position)?.id ?: ""
            val salonLocation: String = viewModel.salonList.value?.get(position)?.addressToString() ?: ""

            // send chosen salon id and location back to previous activity
            replyIntent.putExtra("salonId", salonId)
            replyIntent.putExtra("salonLocation", salonLocation)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }

//    private fun setupSalonCityFilterSpinner() {
//        // Setup class name picker
//        val filterSpinner: Spinner = binding.salonCityFilter
//
//
//        // Create an adapter to display list of filter options
//        var items: ArrayList<String> = viewModel.salonCityList.value!!
//        items.add(0, "Tất cả") // Add default value
//
//        val filterSpinnerAdapter = object : ArrayAdapter<String>(
//            this,
//            android.R.layout.simple_spinner_item,
//            items
//        ) {
//
//            override fun getDropDownView(
//                position: Int,
//                convertView: View?,
//                parent: ViewGroup
//            ): View {
//                return super.getDropDownView(position, convertView, parent) as TextView
//            }
//        }
//
//        // Set layout for all rows of spinner
//        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Assign adapter to the spinner
//        filterSpinner.adapter = filterSpinnerAdapter
//
//        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            @RequiresApi(Build.VERSION_CODES.N)
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                if(position  > 0) {
//                    val selectedCity = items[position]
//                    // Filter appointments by subId
//                    var salonToBeHiddenIndexWhenFilterCity: ArrayList<Int> = ArrayList()
//                    viewModel.salonList.value!!.forEach { salon ->
//                        if(selectedCity != salon.address?.get("city")!!)
//                            salonToBeHiddenIndexWhenFilterCity.add(viewModel.salonList.value!!.indexOf(salon))
//                    }
//
//
//                    salonListAdapter.setSalonToBeHiddenIndexWhenFilterCity(salonToBeHiddenIndexWhenFilterCity)
//                }
//                else
//                    salonListAdapter.setSalonToBeHiddenIndexWhenFilterCity(null)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//
//            }
//        }
//    }
//
//    private fun setupSalonDistrictFilterSpinner() {
//        // Setup class name picker
//        val filterSpinner: Spinner = binding.salonDistrictFilter
//
//
//        // Create an adapter to display list of filter options
//        var items: ArrayList<String> = viewModel.salonDistrictList.value!!
//        items.add(0, "Tất cả") // Add default value
//
//        val filterSpinnerAdapter = object : ArrayAdapter<String>(
//            this,
//            android.R.layout.simple_spinner_item,
//            items
//        ) {
//
//            override fun getDropDownView(
//                position: Int,
//                convertView: View?,
//                parent: ViewGroup
//            ): View {
//                return super.getDropDownView(position, convertView, parent) as TextView
//            }
//        }
//
//        // Set layout for all rows of spinner
//        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Assign adapter to the spinner
//        filterSpinner.adapter = filterSpinnerAdapter
//
//        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            @RequiresApi(Build.VERSION_CODES.N)
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                if(position  > 0) {
//                    val selectedDistrict = items[position]
//                    // Filter appointments by subId
//                    var salonToBeHiddenIndexWhenFilterDistrict: ArrayList<Int> = ArrayList()
//                    viewModel.salonList.value!!.forEach { salon ->
//                        if(selectedDistrict != salon.address?.get("district")!!)
//                            salonToBeHiddenIndexWhenFilterDistrict.add(viewModel.salonList.value!!.indexOf(salon))
//                    }
//
//
//                    salonListAdapter.setSalonToBeHiddenIndexWhenFilterDistrict(salonToBeHiddenIndexWhenFilterDistrict)
//                }
//                else
//                    salonListAdapter.setSalonToBeHiddenIndexWhenFilterDistrict(null)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//
//            }
//        }
//    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}