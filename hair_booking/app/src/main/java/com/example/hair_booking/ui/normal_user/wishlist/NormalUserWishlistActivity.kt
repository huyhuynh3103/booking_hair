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
import com.example.hair_booking.ui.manager.stylist.ManagerStylistDetailActivity
import com.example.hair_booking.ui.manager.stylist.ManagerStylistListViewModel
import com.example.hair_booking.ui.manager.stylist.StylistRecycleViewAdapter
import com.example.hair_booking.ui.normal_user.salon.NormalUserSalonDetailActivity
import kotlinx.coroutines.launch

class NormalUserWishlistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNormalUserWishlistBinding
    private val viewModel: NormalUserWishlistViewModel by viewModels()

    private lateinit var adapter: WishlistRecyclerViewAdapter
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

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

        // Get user ID
        val userID = "UoNSp86NweiuniUgZWMz"

        lifecycleScope.launch {
            binding.viewModel!!.getUserDetail(userID)

            binding.viewModel!!.getWishlist()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun setOnClickListenerForItem() {
        adapter.onItemClick = {
            // Create an intent to send data
            val intent = Intent(this, NormalUserSalonDetailActivity::class.java)

            // send chosen service id and name back to previous activity
            intent.putExtra("SalonID", it.id)

            startActivity(intent)
        }
    }
}