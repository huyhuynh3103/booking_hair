package com.example.hair_booking.ui.admin.discount.overview


import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminDiscountListBinding
import com.example.hair_booking.ui.admin.discount.add_new_discount.AdminAddNewDiscountActivity
import com.example.hair_booking.ui.admin.discount.add_new_discount.AdminAddNewDiscountViewModel
import com.example.hair_booking.ui.admin.discount.edit_discount.AdminEditDiscountActivity
import kotlinx.coroutines.launch

class AdminDiscountListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDiscountListBinding
    private val REQUEST_CODE_DETAIL: Int = 1111

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: AdminDiscountListViewModel by viewModels()

    private lateinit var discountListAdapter: AdminDiscountListAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_discount_list)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        setupDiscountListAdapter()


        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        observeOnClickEvent()
    }

    private fun setupDiscountListAdapter() {
        // Create adapter for salon list recyclerview
        discountListAdapter = AdminDiscountListAdapter(viewModel)
        binding.adminDiscountListRecyclerView.adapter = discountListAdapter

        // Assign linear layout for appointment list recyclerview
        binding.adminDiscountListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.adminDiscountListRecyclerView.addItemDecoration(itemDecoration)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeOnClickEvent() {
        // Observe add button onclick event
        viewModel.addNewDiscountBtnClicked.observe(this, androidx.lifecycle.Observer {
            moveToAddNewDiscountScreen()
        })

        // Observe edit button onclick event
        viewModel.editDiscountBtnClicked.observe(this, androidx.lifecycle.Observer {
            moveToEditDiscountScreen()
        })

        // Observe delete button onclick event
        viewModel.deleteDiscountBtnClicked.observe(this, androidx.lifecycle.Observer {
            displayDeleteAlertDialog()
        })
    }

    private fun moveToAddNewDiscountScreen() {
        val intent = Intent(this, AdminAddNewDiscountActivity::class.java)
        startActivity(intent)
    }

    private fun moveToEditDiscountScreen() {
        val intent = Intent(this, AdminEditDiscountActivity::class.java)

        intent.putExtra("discountId", viewModel.discountToBeEditedId.value)

        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDeleteAlertDialog() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Xóa khuyến mãi")
        //set message for alert dialog
        builder.setMessage("Bạn có thật sự muốn xóa khuyến mãi này không ?")
        builder.setIcon(android.R.drawable.ic_delete)

        //performing positive action
        builder.setPositiveButton("Có"){dialogInterface, which ->
            lifecycleScope.launch {
                val ack = viewModel.deleteDiscount()
                if(ack) {
                    runOnUiThread {
                        displayUpdateSuccessDialog("Xóa khuyến mãi thành công")
                    }
                }
            }
        }
        //performing negative action
        builder.setNegativeButton("Không"){dialogInterface, which ->
            // do nothing
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun displayUpdateSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Thành công")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(getDrawable(R.drawable.ic_baseline_check_24))

        //performing positive action
        builder.setPositiveButton("Ok"){dialogInterface, which ->
//            binding.adminServiceListRecyclerView.adapter?.notifyDataSetChanged()
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

//    // Back to main screen when click back button
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        finish()
//        return true
//    }

    // Back to main screen when click back button
    override fun onBackPressed() {
        super.onBackPressed()
        parent.recreate()
    }
}