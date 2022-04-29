package com.example.hair_booking.ui.admin.home

import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminHomeBinding
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Statistics
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.ui.manager.home.ManagerHomeViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AdminHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val adminHomeViewModel: AdminHomeViewModel by viewModels()
    private lateinit var binding:ActivityAdminHomeBinding
    //private var salonList = adminHomeViewModel.testgetSalonList()!!
    private var salonList = ArrayList<Salon>()
    private var pairMonthList = ArrayList<Pair<Pair<Int, Int>, Long>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_admin_home)
        binding.lifecycleOwner = this@AdminHomeActivity
        binding.viewModel = adminHomeViewModel
        setupUI()
        initBarChart()
        //salonList =
        setupFilterSpinner()


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.nav_home_admin->{

            }
            R.id.nav_branch_admin->{

            }
            R.id.nav_service_admin->{

            }
            R.id.nav_discount_admin->{

            }
            R.id.nav_account_manager_admin->{

            }
            R.id.nav_account_user_admin->{

            }
            R.id.nav_sign_out_admin->{
                AuthRepository.signOut()
                finish()
            }
        }
        binding.drawerLayoutAdmin.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupUI() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,binding.drawerLayoutAdmin,toolbar,
            R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        binding.drawerLayoutAdmin.addDrawerListener(toggle)

        toggle.syncState()
        binding.navigationViewAdmin.setNavigationItemSelectedListener(this)
        binding.navigationViewAdmin.menu.findItem(R.id.nav_home_admin).isCheckable = true
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        // handling back button pressed event
        if(binding.drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayoutAdmin.closeDrawer(GravityCompat.START)
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
            Log.d(ContentValues.TAG, "getAxisLabel: index $index")
            label = pairMonthList[index].first.first.toString() + "/" + pairMonthList[index].first.second.toString()
            return label
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawBarChart(pos: Int)
    {
        val entries: ArrayList<BarEntry> = ArrayList()
        var salonId = ""

        GlobalScope.launch {
            salonList = adminHomeViewModel.getSalonList()!!
            salonId = salonList[pos].id
            pairMonthList = adminHomeViewModel.getRevenueOfNLastMonths(salonId)!!
            //statisticsList = getShiftList(map)
            //now draw bar chart with dynamic data

            //you can replace this data object with  your custom object
            for (i in pairMonthList.indices) {
                val (time, amount) = pairMonthList[i]
                Log.d("salontest", time.first.toString())
                entries.add(BarEntry(i.toFloat(), amount.toFloat()))
            }

            val barDataSet = BarDataSet(entries, "Statistics")
            barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            barDataSet.valueTextSize = 16f

            val data = BarData(barDataSet)

            binding.chart.xAxis.textSize = 14f
            binding.chart.extraBottomOffset = 2f

            binding.chart.data = data
            binding.chart.notifyDataSetChanged();

            binding.chart.invalidate()
        }
    }

    private fun resetBarChart() {
        val entries: ArrayList<BarEntry> = ArrayList()

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

    private fun setupFilterSpinner() {

        // Setup class name picker
        val filterSpinner: Spinner = binding.salonListFilter


        // Create an adapter to display list of filter options
        //var items: ArrayList<String> = arrayListOf(
            //"Dịch vụ đặt nhiều nhất",
            //"Ca đặt nhiều nhất",
            //"Doanh thu ngày",
            //"Doanh thu tháng")
        //Log.d("salontest1", salonList[0].name!!)
        //var items = ArrayList<String>()
        //for (i in salonList?.indices!!) {
            //val item = salonList!![i].name
            //items.add(item!!)
        //}
        val filterSpinnerAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item
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
                resetBarChart()
                drawBarChart(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }
}