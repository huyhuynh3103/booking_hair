package com.example.hair_booking.ui.admin.discount.edit_discount

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminEditDiscountBinding
import com.example.hair_booking.services.booking.DateServices
import com.example.hair_booking.ui.admin.discount.add_new_discount.ServicePickerSpinnerAdapter
import com.example.hair_booking.ui.admin.discount.overview.AdminDiscountListActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.*
import java.util.*


class AdminEditDiscountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminEditDiscountBinding

    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: AdminEditDiscountViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_edit_discount)

        val discountToBeEditedId: String? = intent.getStringExtra("discountId")
        if(discountToBeEditedId != null) {
            lifecycleScope.launch {
                async {
                    viewModel.prepareData(discountToBeEditedId)
                }.await()

                // Assign view model to binding
                binding.viewModel = viewModel

                // Tell binding to observe the life cycle of this activity
                binding.lifecycleOwner = this@AdminEditDiscountActivity

                // Enable back button
                val toolbar: Toolbar = findViewById(R.id.toolbar)
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                observeOnClickEvent()

                viewModel.serviceTitleList.observe(this@AdminEditDiscountActivity, {
                    setupServicePickerSpinner()
                })
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun observeOnClickEvent() {
        // Observe date edit text onclick event to display date picker dialog
        viewModel.dateAppliedEditTextClicked.observe(this, androidx.lifecycle.Observer {
            displayDateAppliedPickerDialog()
        })

        // Observe date edit text onclick event to display date picker dialog
        viewModel.dateExpiredEditTextClicked.observe(this, androidx.lifecycle.Observer {
            displayDateExpiredPickerDialog()
        })

        // Observe confirm button onclick event
        viewModel.confirmBtnClicked.observe(this, androidx.lifecycle.Observer {

            val title = binding.adminEditDiscountTitleEditText.text.toString()
            val description = binding.adminEditDiscountDescriptionEditText.text.toString()
            val requiredPoint = binding.adminEditDiscountRequiredPointEditText.text.toString()
            val percent = binding.adminEditDiscountPercentEditText.text.toString()
            val dateApplied = binding.adminEditDiscountDateAppliedEditText.text.toString()
            val dateExpired = binding.adminEditDiscountDateExpiredEditText.text.toString()
            val serviceApplied = viewModel.discountServiceAppliedId.value

            var tmpRequiredPoint = ""
            if(requiredPoint.length > 1) {
                tmpRequiredPoint = requiredPoint
                while(tmpRequiredPoint[0] == '0') {
                    tmpRequiredPoint = tmpRequiredPoint.substring(1)
                }
            }
            else
                tmpRequiredPoint = requiredPoint

            var tmpPercent = ""
            if(percent.length > 1) {
                tmpPercent = percent
                while(tmpPercent[0] == '0' && tmpPercent[1] != '.') {
                    tmpPercent = tmpPercent.substring(1)
                }
            }
            else
                tmpPercent = percent


            val inputs = hashMapOf(
                "title" to title,
                "description" to description,
                "requiredPoint" to tmpRequiredPoint.toLong(),
                "percent" to tmpPercent,
                "dateApplied" to dateApplied,
                "dateExpired" to dateExpired,
                "serviceApplied" to serviceApplied
            )
            if(viewModel.checkEmptyFieldsExist(inputs))
                displayEmptyFieldsWarning()
            else {
                if((tmpPercent.toFloat() <= 0F || tmpPercent.toFloat() > 1F)) {
                    displayInvalidPercentWarning()
                }
                else if(tmpRequiredPoint.toLong() <= 0) {
                    displayInvalidRequiredPointWarning()
                }
                else {
                    GlobalScope.launch {
                        val ack = viewModel.updateDiscount(inputs)
                        if(!ack) {
                            runOnUiThread {
                                displayEditDiscountFailed()
                            }
                        }
                        else {
                            runOnUiThread {
                                displayEditSuccessDialog("Chỉnh sửa khuyến mãi thành công")
                            }

                        }
                    }
                }

            }


        })
    }

    private fun moveToDiscountListScreen() {
        val intent = Intent(this, AdminDiscountListActivity::class.java)
        startActivity(intent)
    }


    // Back to previous screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun displayEmptyFieldsWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Vui lòng điền đầy đủ các trường!!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun displayInvalidRequiredPointWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Số điểm phải lớn hơn 0 !!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun displayInvalidPercentWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Phần trăm khuyến mãi phải lớn hơn 0 và nhỏ hơn hoặc bằng 1!!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun displayEditDiscountFailed() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thất bại")
        builder.setMessage("Chỉnh sửa khuyến mãi thất bại!!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun displayEditSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Thành công")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(getDrawable(R.drawable.ic_baseline_check_24))

        //performing positive action
        builder.setPositiveButton("Ok"){dialogInterface, which ->
            moveToDiscountListScreen()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun displayDateAppliedPickerDialog() {
        val now = Calendar.getInstance() // Get current time

        // Init date picker dialog with current time selected
        val dpd = DatePickerDialog.newInstance(
            { view, year, monthOfYear, dayOfMonth ->
                // Add leading zero if has
                val daySelected: String = if(dayOfMonth < 10) "0$dayOfMonth" else "" + dayOfMonth
                val monthSelected: String = if(monthOfYear < 10) "0" + (monthOfYear + 1) else "" + dayOfMonth

                // Concatenate to a full date string
                val dateInStringFormat = "$daySelected/$monthSelected/$year"
                binding.viewModel!!.setChosenDateApplied(dateInStringFormat)
            },
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH], // Inital day selection
        )


        // Set min date = current date
        dpd.minDate = now

        // Show date picker dialog
        dpd.show(supportFragmentManager, "Datepickerdialog");
    }

    private fun displayDateExpiredPickerDialog() {
        val now = Calendar.getInstance() // Get current time

        // Init date picker dialog with current time selected
        val dpd = DatePickerDialog.newInstance(
            { view, year, monthOfYear, dayOfMonth ->
                // Add leading zero if has
                val daySelected: String = if(dayOfMonth < 10) "0$dayOfMonth" else "" + dayOfMonth
                val monthSelected: String = if(monthOfYear < 10) "0" + (monthOfYear + 1) else "" + dayOfMonth

                // Concatenate to a full date string
                val dateInStringFormat = "$daySelected/$monthSelected/$year"
                if(DateServices.isAfter(dateInStringFormat, viewModel.discountDateApplied.value!!))
                    binding.viewModel!!.setChosenDateExpired(dateInStringFormat)
                else
                    displayInvalidDateExpiredWarning()
            },
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH], // Inital day selection
        )


        // Set min date = current date
        dpd.minDate = now

        // Show date picker dialog
        dpd.show(supportFragmentManager, "Datepickerdialog");
    }

    private fun displayInvalidDateExpiredWarning() {
        // Show warning dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cảnh báo")
        builder.setMessage("Ngày hết hạn không hợp lệ!!!")

        builder.setPositiveButton("Ok") { dialog, which ->
            // Do nothing
        }
        builder.show()
    }

    private fun setupServicePickerSpinner() {
        val servicePickerSpinner = binding.servicePickerSpinner

        val serviceItems = ArrayList(viewModel.serviceTitleList.value!!)
        serviceItems.add(0, "Chọn dịch vụ áp dụng") // add placeholder

        // Assign adapter to spinner
        servicePickerSpinner.adapter = ServicePickerSpinnerAdapter(this, serviceItems)

        servicePickerSpinner.setSelection(serviceItems.indexOf(viewModel.discountServiceApplied.value!!))
        servicePickerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position - 1 >= 0) {
                    viewModel.setChosenService(viewModel.serviceList.value!![position - 1].id, viewModel.serviceList.value!![position - 1].title!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

}