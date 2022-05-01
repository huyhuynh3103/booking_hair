package com.example.hair_booking.ui.admin.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityAdminHomeBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.google.android.material.navigation.NavigationView

class AdminHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding:ActivityAdminHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_admin_home)
        binding.lifecycleOwner = this@AdminHomeActivity

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,binding.drawerLayoutAdmin,toolbar,
            R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        binding.drawerLayoutAdmin.addDrawerListener(toggle)

        toggle.syncState()
        binding.navigationViewAdmin.setNavigationItemSelectedListener(this)
        binding.navigationViewAdmin.menu.findItem(R.id.nav_home_admin).isCheckable = true

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
    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        // handling back button pressed event
        if(binding.drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayoutAdmin.closeDrawer(GravityCompat.START)
        }
    }
}