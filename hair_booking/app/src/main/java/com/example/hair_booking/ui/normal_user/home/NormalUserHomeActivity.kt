package com.example.hair_booking.ui.normal_user.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.hair_booking.R
import com.example.hair_booking.firebase.Database
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class NormalUserHomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private val FRAGMENT_HOME = 0
    private val FRAGMENT_WISH_LIST = 1
    private val FRAGMENT_MEMBERSHIP = 2

    private var mCurrentFragment = FRAGMENT_HOME

    private var mDrawerLayout: DrawerLayout? = null
    // Get firestore instance
    private val firebaseInstance: FirebaseFirestore = Database.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // config toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // config navigation view -> add toggle button
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this,mDrawerLayout,toolbar,
            R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        mDrawerLayout?.addDrawerListener(toggle)
        toggle.syncState()


        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        // set clicked listener on each item in navigation view
        navigationView.setNavigationItemSelectedListener(this)

        // Default home fragment
        //replaceFragement()
        navigationView.menu.findItem(R.id.nav_home).isChecked = true
    }
    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // override interface NavigationDrawe.onNavigationItemSelected
        val id = item.itemId
        when(id){
            R.id.nav_home->{
                if(mCurrentFragment!=FRAGMENT_HOME){
                    //  replace and re-assign mCurrent Fragment
                    //  replaceFragement(fragment home)
                    mCurrentFragment = FRAGMENT_HOME
                }
            }
            R.id.nav_wish_list->{

            }
            R.id.nav_membership->{

            }
            R.id.nav_my_profile->{

            }
            R.id.nav_change_password->{

            }
        }

        mDrawerLayout?.closeDrawer(GravityCompat.START)
        return true
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
    private fun replaceFragement(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame,fragment)
        transaction.commit()
    }
}