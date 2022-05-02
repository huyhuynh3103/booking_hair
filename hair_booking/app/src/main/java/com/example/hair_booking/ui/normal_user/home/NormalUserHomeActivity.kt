package com.example.hair_booking.ui.normal_user.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityNormalUserHomeBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.ui.authentication.LogInActivity
import com.example.hair_booking.ui.normal_user.booking.BookingActivity
import com.example.hair_booking.ui.normal_user.history.overview.HistoryBooking
import com.example.hair_booking.ui.normal_user.profile.NormalUserProfileActivity
import com.example.hair_booking.ui.normal_user.salon.NormalUserSalonDetailActivity
import com.example.hair_booking.ui.normal_user.wishlist.NormalUserWishlistActivity
import com.google.android.material.navigation.NavigationView

class NormalUserHomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    val REQUEST_CODE = 1111
    private var mDrawerLayout: DrawerLayout? = null
    private val salonViewModel: SalonViewModel by viewModels()
    private lateinit var binding: ActivityNormalUserHomeBinding
    private lateinit var salonAdapter: SalonAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_normal_user_home)
        binding.viewModel = salonViewModel
        binding.lifecycleOwner = this@NormalUserHomeActivity
        setupUI()
        setUserProfile()
        //setupObserver()
        salonAdapter.onItemClick = { salon ->
            val intent = Intent(this,NormalUserSalonDetailActivity::class.java)
            intent.putExtra("SalonID", salon.id)
            startActivity(intent)
        }

    }

    private fun setUserProfile() {

        val userProfile = AuthRepository.getCurrentUser()
        if(userProfile?.displayName!=null){
            binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.nameTextView).text =
                userProfile.displayName.toString()
        }
        else{
            binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.nameTextView).visibility = View.GONE
        }
        if(userProfile?.email!=null){
            binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.gmailTextView).text =
                userProfile.email.toString()
        }
        else{
            binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.gmailTextView).visibility = View.GONE
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
                startActivity(Intent(this, NormalUserWishlistActivity::class.java))
            }
            R.id.nav_history->{
                startActivity(Intent(this,HistoryBooking::class.java))

            }
            R.id.nav_membership->{

            }
            R.id.nav_my_profile->{
                startActivity(Intent(this,NormalUserProfileActivity::class.java))
            }
            R.id.nav_change_password->{

            }
            R.id.nav_sign_out->{
                AuthRepository.signOut()
                val intent = Intent(this, LogInActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }

        mDrawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }
    private fun setupUI() {
        binding.salonListRecycleView.layoutManager = GridLayoutManager(this,2)
        salonAdapter = SalonAdapter()
        binding.salonListRecycleView.adapter = salonAdapter
        mDrawerLayout = binding.drawerLayout
        binding.salonListRecycleView.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.HORIZONTAL
            )
        )

        val toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout,toolbar,
            R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        binding.drawerLayout.addDrawerListener(toggle)

        toggle.syncState()


        // set clicked listener on each item in navigation view
        binding.navigationView.setNavigationItemSelectedListener(this)


        binding.navigationView.menu.findItem(R.id.nav_home).isChecked = true
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