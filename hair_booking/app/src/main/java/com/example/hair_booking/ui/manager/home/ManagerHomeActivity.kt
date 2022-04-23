package com.example.hair_booking.ui.manager.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerHomeBinding
import com.example.hair_booking.model.Statistics
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.ui.manager.appointment.overview.ManagerAppointmentListActivity
import com.example.hair_booking.ui.manager.profile.ManagerProfileActivity
import com.example.hair_booking.ui.manager.stylist.ManagerStylistListActivity
import com.example.hair_booking.ui.normal_user.home.SalonAdapter
import com.example.hair_booking.ui.normal_user.home.SalonViewModel
import com.google.android.material.navigation.NavigationView
import com.journeyapps.barcodescanner.ScanOptions
import android.widget.Toast

import com.journeyapps.barcodescanner.ScanContract

import androidx.activity.result.ActivityResultLauncher
import com.journeyapps.barcodescanner.ScanIntentResult


class ManagerHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mDrawerLayout: DrawerLayout? = null
    private val salonViewModel: SalonViewModel by viewModels()
    private lateinit var binding: ActivityManagerHomeBinding
    private lateinit var salonAdapter: SalonAdapter
    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>
    private var serviceList = ArrayList<Statistics>()
    private var shiftList = ArrayList<Statistics>()
    private var pairDayList = ArrayList<Pair<String, Long>>()
    private var pairMonthList = ArrayList<Pair<Pair<Int, Int>, Long>>()
    private var typeChart = 0
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_manager_home)
        binding.lifecycleOwner = this@ManagerHomeActivity
        barcodeLauncher = registerForActivityResult(ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this@ManagerHomeActivity, "Cancelled", Toast.LENGTH_LONG).show()
                // Start Activity for this
            } else {
                Toast.makeText(this@ManagerHomeActivity,
                    "Scanned: " + result.contents,
                    Toast.LENGTH_LONG).show()

            }
        }
        setupUI()
        setUserProfile()
        initBarChart()
        setupFilterSpinner()
    }

    private fun setUserProfile() {
        val userProfile = AuthRepository.getCurrentUser()
//        bindindHeaderNavigationBinding.nameTextView.setText(userProfile!!.displayName.toString())
//        bindindHeaderNavigationBinding.gmailTextView.setText(userProfile.email.toString())
    }

    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.nav_home_manager->{

            }
            R.id.nav_scan_qr->{
                val options = ScanOptions()
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                options.setPrompt("Quét QR Code")
                options.setCameraId(0) // Use a specific camera of the device
                options.setBeepEnabled(false)
                options.setBarcodeImageEnabled(true)
                barcodeLauncher.launch(options)
            }
            R.id.nav_schedule_manager->{
                startActivity(Intent(this, ManagerAppointmentListActivity::class.java))

            }
            R.id.nav_stylist_list->{
                startActivity(Intent(this,ManagerStylistListActivity::class.java))
            }
            R.id.nav_my_profile_manager->{
                startActivity(Intent(this, ManagerProfileActivity::class.java))
            }
            R.id.nav_change_password_manager->{

            }
            R.id.nav_sign_out_manager->{
                AuthRepository.signOut()
                finish()
            }
        }

        mDrawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupUI(){

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout,toolbar,
            R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        binding.drawerLayout.addDrawerListener(toggle)

        toggle.syncState()


        // set clicked listener on each item in navigation view
        binding.navigationViewManager.setNavigationItemSelectedListener(this)


        binding.navigationViewManager.menu.findItem(R.id.nav_home_manager).isChecked = true
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        // handling back button pressed event
        if(mDrawerLayout!!.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout?.closeDrawer(GravityCompat.START)
        }
    }

    private fun initBarChart() {


//        hide grid lines
        binding.chart.axisLeft.setDrawGridLines(true)
        binding.chart.axisLeft.textSize = 15f
        binding.chart.axisLeft.setDrawAxisLine(true)
        val xAxis: XAxis = binding.chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)

        //remove right y-axis
        binding.chart.axisRight.isEnabled = false

        //remove legend
        binding.chart.legend.isEnabled = false


        //remove description label
        binding.chart.description.isEnabled = false

        //binding.chart.extraBottomOffset = 2f


        //add animation
        binding.chart.animateY(2000)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        //xAxis.textSize = 15f
        xAxis.labelRotationAngle = +0f

    }


    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            var label : String = ""
            Log.d(TAG, "getAxisLabel: index $index")
            if (typeChart == 0) {
                if (index < serviceList.size) {
                    label = serviceList[index].name
                }
            }
            else if (typeChart == 1) {
                label = shiftList[index].name
            }
            else if (typeChart == 2) {
                label = pairDayList[index].first
            }
            else if (typeChart == 3) {
                label = pairMonthList[index].first.first.toString() + "/" + pairMonthList[index].first.second.toString()
            }
            return label
        }
    }


    // simulate api call
    // we are initialising it directly
    private fun getServiceList(map: Map<String, Int>?): ArrayList<Statistics> {
        if (map != null) {
            for (key in map.keys) {
                serviceList.add(Statistics(key, map[key]!!))
            }
        }

        return serviceList
    }

    private fun getShiftList(map: Map<String, Int>?): ArrayList<Statistics> {
        if (map != null) {
            for (key in map.keys) {
                shiftList.add(Statistics(key, map[key]!!))
            }
        }

        return shiftList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawBarChart(type: Int)
    {
        var map : Map<String, Int>? = emptyMap()
        val entries: ArrayList<BarEntry> = ArrayList()

        if (type == 0) {
            GlobalScope.launch {
                map = managerHomeViewModel.getAmountOfServicesBooked()

                serviceList = getServiceList(map)
                //now draw bar chart with dynamic data

                //you can replace this data object with  your custom object
                for (i in serviceList.indices) {
                    val amount = serviceList[i]
                    entries.add(BarEntry(i.toFloat(), amount.amount.toFloat()))
                }


                val barDataSet = BarDataSet(entries, "Statistics")
                barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
                barDataSet.valueTextSize = 20f

                val data = BarData(barDataSet)

                binding.chart.xAxis.textSize = 15f
                binding.chart.extraBottomOffset = 3f

                binding.chart.data = data
                binding.chart.notifyDataSetChanged()
                binding.chart.invalidate()
            }
        }
        else if (type == 1)
        {
            GlobalScope.launch {
                map = managerHomeViewModel.getAmountOfShiftsBooked()
                shiftList = getShiftList(map)
                //now draw bar chart with dynamic data

                //you can replace this data object with  your custom object
                for (i in shiftList.indices) {
                    val amount = shiftList[i]
                    entries.add(BarEntry(i.toFloat(), amount.amount.toFloat()))
                }


                val barDataSet = BarDataSet(entries, "Statistics")
                barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
                barDataSet.valueTextSize = 20f

                val data = BarData(barDataSet)

                binding.chart.xAxis.textSize = 20f
                binding.chart.extraBottomOffset = 4f

                binding.chart.data = data
                binding.chart.notifyDataSetChanged();

                binding.chart.invalidate()
            }
        }

        else if (type == 2)
        {
            GlobalScope.launch {
                pairDayList = managerHomeViewModel.getRevenueOfNLastDays()!!
                //statisticsList = getShiftList(map)
                //now draw bar chart with dynamic data

                //you can replace this data object with  your custom object
                for (i in pairDayList.indices) {
                    val (day, amount) = pairDayList[i]
                    entries.add(BarEntry(i.toFloat(), amount.toFloat()))
                }

                val barDataSet = BarDataSet(entries, "Statistics")
                barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
                barDataSet.valueTextSize = 16f

                val data = BarData(barDataSet)

                binding.chart.xAxis.textSize = 12f

                binding.chart.data = data
                binding.chart.notifyDataSetChanged();

                binding.chart.invalidate()
            }
        }
        else if (type == 3)
        {
            GlobalScope.launch {
                pairMonthList = managerHomeViewModel.getRevenueOfNLastMonths()!!
                //statisticsList = getShiftList(map)
                //now draw bar chart with dynamic data

                //you can replace this data object with  your custom object
                for (i in pairMonthList.indices) {
                    val (time, amount) = pairMonthList[i]
                    entries.add(BarEntry(i.toFloat(), amount.toFloat()))
                }

                val barDataSet = BarDataSet(entries, "Statistics")
                barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
                barDataSet.valueTextSize = 16f

                val data = BarData(barDataSet)

                binding.chart.xAxis.textSize = 14f

                binding.chart.data = data
                binding.chart.notifyDataSetChanged();

                binding.chart.invalidate()
            }
        }
    }

    private fun resetBarChart(type : Int) {
        val entries: ArrayList<BarEntry> = ArrayList()
        if (type == 0) {
            //now draw bar chart with dynamic data
            serviceList.clear()
            //you can replace this data object with  your custom object
            for (i in serviceList.indices) {
                val amount = serviceList[i]
                entries.add(BarEntry(i.toFloat(), amount.amount.toFloat()))
            }


            val barDataSet = BarDataSet(entries, "Statistics")
            barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            barDataSet.valueTextSize = 20f

            val data = BarData(barDataSet)



            binding.chart.data = data
            binding.chart.notifyDataSetChanged()
            binding.chart.invalidate()
        }
        else if (type == 1)
        {
            //now draw bar chart with dynamic data
            shiftList.clear()
            //you can replace this data object with  your custom object
            for (i in shiftList.indices) {
                val amount = shiftList[i]
                entries.add(BarEntry(i.toFloat(), amount.amount.toFloat()))
            }


            val barDataSet = BarDataSet(entries, "Statistics")
            barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            barDataSet.valueTextSize = 20f

            val data = BarData(barDataSet)



            binding.chart.data = data
            binding.chart.notifyDataSetChanged()
            binding.chart.invalidate()
        }

        else if (type == 2)
        {
            //now draw bar chart with dynamic data
            pairDayList.clear()
            //you can replace this data object with  your custom object
            for (i in pairDayList.indices) {
                val (day ,amount) = pairDayList[i]
                entries.add(BarEntry(i.toFloat(), amount.toFloat()))
            }


            val barDataSet = BarDataSet(entries, "Statistics")
            barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            barDataSet.valueTextSize = 10f

            val data = BarData(barDataSet)



            binding.chart.data = data
            binding.chart.notifyDataSetChanged()
            binding.chart.invalidate()
        }

        else if (type == 3)
        {
            //now draw bar chart with dynamic data
            pairMonthList.clear()
            //you can replace this data object with  your custom object
            for (i in pairMonthList.indices) {
                val (time ,amount) = pairMonthList[i]
                entries.add(BarEntry(i.toFloat(), amount.toFloat()))
            }


            val barDataSet = BarDataSet(entries, "Statistics")
            barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            barDataSet.valueTextSize = 20f

            val data = BarData(barDataSet)



            binding.chart.data = data
            binding.chart.notifyDataSetChanged()
            binding.chart.invalidate()
        }
    }

    private fun setupFilterSpinner() {
        // Setup class name picker
        val filterSpinner: Spinner = binding.statisticsListFilter


        // Create an adapter to display list of filter options
        var items: ArrayList<String> = arrayListOf(
            "Dịch vụ đặt nhiều nhất",
            "Ca đặt nhiều nhất",
            "Doanh thu ngày",
            "Doanh thu tháng")
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
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position  > 0) {
                    typeChart = position
                    resetBarChart(typeChart)
                    drawBarChart(typeChart)
                }
                else {
                    typeChart = 0
                    resetBarChart(typeChart)
                    drawBarChart(typeChart)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }
}