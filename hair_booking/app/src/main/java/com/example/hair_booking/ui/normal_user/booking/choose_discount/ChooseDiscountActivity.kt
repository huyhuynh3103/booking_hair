package com.example.hair_booking.ui.normal_user.booking.choose_discount

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityChooseDiscountBinding
import com.example.hair_booking.services.local.DiscountServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ChooseDiscountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseDiscountBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ChooseDiscountViewModel by viewModels()
    private lateinit var userId: String
    private var userCurrentPoint: Long = 0
    private lateinit var chosenDate: String
    private lateinit var discountListAdapter: DiscountListAdapter
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get current user id and chosen date
        userId = intent.getStringExtra("userId").toString()
        userCurrentPoint = intent.getLongExtra("userCurrentPoint", 0)
        Log.d("xk", "choosediscountactivity: $userCurrentPoint")
        chosenDate = intent.getStringExtra("chosenDate").toString()
        viewModel.setUserId(userId)
        viewModel.setChosenDate(chosenDate)
        viewModel.setupDiscountList(userId, chosenDate)

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
            var discountCanBeApplied: Boolean = false
            discountCanBeApplied = DiscountServices.canApplied(
                userId,
                userCurrentPoint,
                chosenDate,
                viewModel.discountList.value?.get(position)!!.dateApplied!!,
                viewModel.discountList.value?.get(position)!!.requiredPoint!!
            )
            if(discountCanBeApplied) {
                // Create an intent to send data back to previous activity
                val replyIntent = Intent()

                val discountId: String = viewModel.discountList.value?.get(position)?.id ?: ""
                val discountTitle: String = viewModel.discountList.value?.get(position)?.title ?: ""
                val discountPercent: Float? = viewModel.discountList.value?.get(position)?.percent?.toFloat()
                // send chosen discount id, title and percent back to previous activity
                replyIntent.putExtra("discountId", discountId)
                replyIntent.putExtra("discountTitle", discountTitle)
                replyIntent.putExtra("discountPercent", discountPercent)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
            else
                displayDiscountCannotAppliedWarning()
        }
    }

    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun displayDiscountCannotAppliedWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Khuyến mãi bạn chọn chưa được áp dụng! Vui lòng chọn khuyến mãi khác")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }
}