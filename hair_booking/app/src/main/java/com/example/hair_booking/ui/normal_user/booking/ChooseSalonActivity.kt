package com.example.hair_booking.ui.normal_user.booking

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityChooseSalonBinding
import com.example.hair_booking.databinding.ActivityChooseStylistBinding

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set onclick event on item in salon list
        setOnSalonItemClickedEvent()

    }

    private fun setOnSalonItemClickedEvent() {
        salonListAdapter.onItemClick = {position: Int ->
            val replyIntent = Intent()
            val salonLocation: String = viewModel.salonList.value?.get(position)?.address ?: ""

            // send chosen salon location back to previous activity
            replyIntent.putExtra("salonLocation", salonLocation)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}