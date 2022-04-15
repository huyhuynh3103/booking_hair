package com.example.hair_booking.ui.manager.stylist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityChooseStylistBinding
import com.example.hair_booking.databinding.ActivityManagerStylistListBinding
import com.example.hair_booking.ui.normal_user.booking.StylistListAdapter

class ManagerStylistListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerStylistListBinding
    private val viewModel: ManagerStylistListViewModel by viewModels()

    private lateinit var adapter: StylistRecycleViewAdapter
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

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

        setOnStylistItemClickEvent()
        setOnClickListenerForButton()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setOnStylistItemClickEvent() {
        adapter.onItemClick = {
            // Create an intent to send data
            val intent = Intent(this, ManagerStylistDetailActivity::class.java)

            // send chosen service id and name back to previous activity
            intent.putExtra("Task", "Edit")
            intent.putExtra("StylistID", it.id)

            startActivity(intent)
        }
    }

    private fun setOnClickListenerForButton() {
        binding.btAddStylist.setOnClickListener {
            val intent = Intent(this, ManagerStylistDetailActivity::class.java)

            // send chosen service id and name back to previous activity
            intent.putExtra("Task", "Add")

            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}