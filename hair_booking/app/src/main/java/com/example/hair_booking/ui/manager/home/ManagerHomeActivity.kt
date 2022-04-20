package com.example.hair_booking.ui.manager.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerHomeBinding
import com.example.hair_booking.databinding.ActivityNormalUserHomeBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.ui.manager.appointment.AppointmentListActivity
import com.example.hair_booking.ui.manager.profile.ManagerProfileActivity
import com.example.hair_booking.ui.manager.stylist.ManagerStylistListActivity
import com.example.hair_booking.ui.normal_user.booking.BookingActivity
import com.example.hair_booking.ui.normal_user.home.SalonAdapter
import com.example.hair_booking.ui.normal_user.home.SalonViewModel
import com.example.hair_booking.ui.normal_user.profile.NormalUserProfileActivity
import com.example.hair_booking.ui.normal_user.salon.NormalUserSalonDetailActivity
import com.google.android.material.navigation.NavigationView

class ManagerHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mDrawerLayout: DrawerLayout? = null
    private val salonViewModel: SalonViewModel by viewModels()
    private lateinit var binding: ActivityManagerHomeBinding
    private lateinit var salonAdapter: SalonAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_manager_home)
        binding.lifecycleOwner = this@ManagerHomeActivity
        setupUI()
        setUserProfile()
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
            R.id.nav_schedule_manager->{
                startActivity(Intent(this, AppointmentListActivity::class.java))

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

    private fun setupObserver(){
        salonViewModel.hairSalon.observeForever{
            salonAdapter.setData(it)
        }
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
}