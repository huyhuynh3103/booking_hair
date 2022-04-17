package com.example.hair_booking.ui.manager.stylist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerStylistDetailBinding
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ManagerStylistDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerStylistDetailBinding
    private val viewModel: ManagerStylistDetailViewModel by viewModels()

    private lateinit var adapter: ArrayAdapter<Salon>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_stylist_detail)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter = ArrayAdapter<Salon>(this, R.layout.support_simple_spinner_dropdown_item)
        binding.sWorkplace.adapter = adapter

        setOnClickListenerForButton()

        // Support Menu Action
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load data
        binding.task = intent.getStringExtra("Task")
        if (binding.task == "Edit") {
            lifecycleScope.launch {
                // get selected ID from previous activity
                binding.viewModel?.getStylistDetail(intent.getStringExtra("StylistID").toString())
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun setOnClickListenerForButton() {
        binding.bSaveStylistInfo.setOnClickListener() {
            val id = intent.getStringExtra("StylistID").toString()
            val name = binding.etStylistName.text.toString()
            val avatar = ""
            val description = binding.tvStylistDescription.text.toString()

            lifecycleScope.launch {
                val workPlace =
                    binding.viewModel!!.getSelectedWorkplace(binding.sWorkplace.selectedItemPosition)
                val stylist = Stylist(id, name, avatar, description, workPlace!!, false)

                if (binding.task == "Edit") {
                    binding.viewModel!!.updateStylist(id, stylist)
                } else {
                    binding.viewModel!!.addStylist(stylist)
                }

                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }

        binding.bDeleteStylist.setOnClickListener() {
            val id = intent.getStringExtra("StylistID").toString()

            lifecycleScope.launch {
                binding.viewModel!!.deleteStylist(id)

                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }
    }
}