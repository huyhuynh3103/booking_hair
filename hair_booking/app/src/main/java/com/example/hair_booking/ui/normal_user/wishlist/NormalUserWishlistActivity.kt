package com.example.hair_booking.ui.normal_user.wishlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityManagerStylistListBinding
import com.example.hair_booking.databinding.ActivityNormalUserWishlistBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.ui.manager.stylist.ManagerStylistDetailActivity
import com.example.hair_booking.ui.manager.stylist.ManagerStylistListViewModel
import com.example.hair_booking.ui.manager.stylist.StylistRecycleViewAdapter
import com.example.hair_booking.ui.normal_user.salon.NormalUserSalonDetailActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class NormalUserWishlistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNormalUserWishlistBinding
    private val viewModel: NormalUserWishlistViewModel by viewModels()

    private lateinit var adapter: WishlistRecyclerViewAdapter
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

    private var auth: FirebaseUser? = null

    private val REQUEST_CODE_UPDATE_DATA: Int = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_normal_user_wishlist)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter = WishlistRecyclerViewAdapter()
        itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        binding.rvUserWishlist.adapter = adapter
        binding.rvUserWishlist.layoutManager = LinearLayoutManager(this)
        binding.rvUserWishlist.addItemDecoration(itemDecoration)

        setOnClickListenerForItem()

        // Get account ID
        auth = AuthRepository.getCurrentUser()

        lifecycleScope.launch {
            getData()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        lifecycleScope.launch {
            getData()
        }
    }

    private fun setOnClickListenerForItem() {
        adapter.onItemClick = {
            // Create an intent to send data
            val intent = Intent(this, NormalUserSalonDetailActivity::class.java)

            // send chosen service id and name back to previous activity
            intent.putExtra("SalonID", it.id)

            startActivityForResult(intent, REQUEST_CODE_UPDATE_DATA)
        }
    }

    private suspend fun getData() {
        binding.viewModel!!.getUserAccount(auth?.email!!)
        binding.viewModel!!.getUserDetail()
        binding.viewModel!!.getWishlist()
    }
}