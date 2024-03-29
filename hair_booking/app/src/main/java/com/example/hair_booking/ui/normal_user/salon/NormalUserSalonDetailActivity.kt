package com.example.hair_booking.ui.normal_user.salon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityNormalUserSalonDetailBinding
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.ui.normal_user.booking.BookingActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NormalUserSalonDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNormalUserSalonDetailBinding
    private val viewModel: NormalUserSalonDetailViewModel by viewModels {NormalUserSalonViewModelFactory(applicationContext)}
    private var auth: FirebaseUser? = null
    private var id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_normal_user_salon_detail)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setOnClickListenerForButton()

        // enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get salon detail
        val intent = intent
        id = intent.getStringExtra("SalonID")



        auth = AuthRepository.getCurrentUser()

        lifecycleScope.launch {
            async {
                binding.viewModel?.getSalonDetail(id!!)
            }.await()
            loadSalonAvatar()

            getData()
            if (binding.viewModel!!.isInWishlist(id!!)) {
                binding.bWishlist.text = "Xóa khỏi danh sách yêu thích"
            }
            else {
                binding.bWishlist.text = "Thêm vào danh sách yêu thích"
            }
        }
    }

    private fun loadSalonAvatar() {
        val salonImageView = binding.ivSalonAvatar

        // Load image from cloudinary url to image view
        if(!viewModel.salon.value!!.avatar.isNullOrEmpty())
            Picasso.with(this)
                .load(viewModel.salon.value!!.avatar)
                .into(salonImageView)
    }

    // back to previous screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private suspend fun getData() {
        binding.viewModel!!.getUserAccount(auth?.email!!)
        binding.viewModel!!.getUserDetail()
        binding.viewModel!!.getWishlist()
    }


    private fun setOnClickListenerForButton() {
        binding.bBooking.setOnClickListener() {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("salonId", id)
            intent.putExtra("salonLocation", viewModel.salon.value?.addressToString())
            startActivity(intent)
        }

        binding.bWishlist.setOnClickListener() {
            lifecycleScope.launch {
                val user = binding.viewModel!!.user.value?.id
                val salon = binding.viewModel!!.getSalonRef(id!!)

                if (!binding.viewModel!!.isInWishlist(id!!)) {
                    dbServices.getNormalUserServices()?.addToWishlist(user!!, salon!!)
                    binding.bWishlist.text = "Xóa khỏi danh sách yêu thích"
                }
                else {
                    dbServices.getNormalUserServices()?.removeFromWishlist(user!!, salon!!)
                    binding.bWishlist.text = "Thêm vào danh sách yêu thích"
                }

                Toast.makeText(applicationContext, "Danh sách yêu thích đã được cập nhật!", Toast.LENGTH_LONG)
                    .show()

                // Update wishlist
                getData()
            }
        }
    }
}