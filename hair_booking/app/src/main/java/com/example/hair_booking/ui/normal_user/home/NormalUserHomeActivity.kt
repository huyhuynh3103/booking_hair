package com.example.hair_booking.ui.normal_user.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityNormalUserHomeBinding
import com.example.hair_booking.ui.normal_user.booking.BookingActivity
import com.example.hair_booking.ui.normal_user.profile.NormalUserProfileActivity
import com.example.hair_booking.ui.normal_user.salon.NormalUserSalonDetailActivity
import com.google.android.material.navigation.NavigationView

class NormalUserHomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private var mDrawerLayout: DrawerLayout? = null
    private val salonViewModel: SalonViewModel by viewModels()
    private lateinit var binding: ActivityNormalUserHomeBinding
    private lateinit var salonAdapter: SalonAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_normal_user_home)
        binding.viewModel = salonViewModel
        binding.lifecycleOwner = this

        setupUI()
        //setupObserver()
        salonAdapter.onItemClick = { salon ->
            val intent = Intent(this,NormalUserSalonDetailActivity::class.java)
            intent.putExtra("id",salon.id)
            startActivity(intent)
        }

    }
    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.nav_home->{

            }
            R.id.nav_schedule->{
                startActivity(Intent(this,BookingActivity::class.java))
            }
            R.id.nav_wish_list->{

            }
            R.id.nav_membership->{

            }
            R.id.nav_my_profile->{
                startActivity(Intent(this,NormalUserProfileActivity::class.java))
            }
            R.id.nav_change_password->{

            }
        }

        mDrawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupObserver(){
        salonViewModel.hairSalon.observeForever{
            salonAdapter.setData(it)
        }
    }
    private fun setupUI(){
        binding.salonListRecycleView.layoutManager = GridLayoutManager(this,2)
        salonAdapter = SalonAdapter()
        binding.salonListRecycleView.adapter = salonAdapter

        binding.salonListRecycleView.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.HORIZONTAL
            )
        )
        // config toolbar
        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,
            R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // set clicked listener on each item in navigation view
        binding.navigationView.setNavigationItemSelectedListener(this)


        binding.navigationView.menu.findItem(R.id.nav_home).isChecked = true
    }

    @Override
    override fun onBackPressed() {
        // handling back button pressed event
        if(mDrawerLayout!!.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout?.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }
}