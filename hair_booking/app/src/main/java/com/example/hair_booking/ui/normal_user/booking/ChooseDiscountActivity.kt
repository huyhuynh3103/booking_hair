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
import com.example.hair_booking.databinding.ActivityChooseDiscountBinding

class ChooseDiscountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseDiscountBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ChooseDiscountViewModel by viewModels()

    private lateinit var discountListAdapter: DiscountListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_discount)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for discount list recyclerview
        discountListAdapter = DiscountListAdapter()
        binding.discountListRecyclerView.adapter = discountListAdapter

        // Assign linear layout for discount list recyclerview
        binding.discountListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.discountListRecyclerView.addItemDecoration(itemDecoration)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set onclick event on item in discount list
        setOnDiscountItemClickedEvent()

    }

    private fun setOnDiscountItemClickedEvent() {
        discountListAdapter.onItemClick = { position: Int ->
            // Create an intent to send data back to previous activity
            val replyIntent = Intent()

//            val salonId: String = viewModel.discountList.value?.get(position)?.id ?: ""
//            val salonLocation: String = viewModel.discountList.value?.get(position)?.addressToString() ?: ""
//
//            // send chosen salon id and location back to previous activity
//            replyIntent.putExtra("salonId", salonId)
//            replyIntent.putExtra("salonLocation", salonLocation)
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