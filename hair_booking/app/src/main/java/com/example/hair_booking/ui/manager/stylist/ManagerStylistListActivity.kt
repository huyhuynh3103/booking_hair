package com.example.hair_booking.ui.manager.stylist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerStylistListBinding
import kotlinx.coroutines.launch

class ManagerStylistListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerStylistListBinding
    private val viewModel: ManagerStylistListViewModel by viewModels()

    private lateinit var adapter: StylistRecycleViewAdapter
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

    private val REQUEST_CODE_UPDATE_DATA: Int = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_stylist_list)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter = StylistRecycleViewAdapter()
        itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        binding.rvStylistList.adapter = adapter
        binding.rvStylistList.layoutManager = LinearLayoutManager(this)
        binding.rvStylistList.addItemDecoration(itemDecoration)

        // Set Action listener
        setOnClickListenerForItem()
        setOnClickListenerForButton()
        setTextChangeListener()

        // enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get manager ID
        val managerID = "eOPpMKdBw9iQL1CDIg28"

        lifecycleScope.launch {
            binding.viewModel!!.getManagerDetail(managerID)

            val salonID = binding.viewModel!!.manager.value?.hairSalon
            binding.viewModel!!.getStylistList(salonID)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        lifecycleScope.launch {
            val salonID = binding.viewModel!!.manager.value?.hairSalon
            binding.viewModel!!.getUpdatedStylistList(salonID)
        }
    }

    private fun setOnClickListenerForItem() {
        adapter.onItemClick = {
            // Create an intent to send data
            val intent = Intent(this, ManagerStylistDetailActivity::class.java)

            // send chosen service id and name back to previous activity
            intent.putExtra("Task", "Edit")
            intent.putExtra("StylistID", it.id)

            startActivityForResult(intent, REQUEST_CODE_UPDATE_DATA)
        }
    }

    private fun setOnClickListenerForButton() {
        binding.btAddStylist.setOnClickListener {
            val intent = Intent(this, ManagerStylistDetailActivity::class.java)

            // send chosen service id and name back to previous activity
            intent.putExtra("Task", "Add")

            startActivityForResult(intent, REQUEST_CODE_UPDATE_DATA)
        }
    }

    private fun setTextChangeListener() {
        binding.actvSearchStylistName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                createData(actvSearch!!.text.toString())
//                listAdapter.notifyDataSetChanged()
                // Update data
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}