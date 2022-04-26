package com.example.hair_booking.ui.normal_user.wishlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices

class NormalUserWishlistViewModel: ViewModel() {
    private val _user: MutableLiveData<NormalUser> = MutableLiveData()
    private val _wishlist: MutableLiveData<ArrayList<Salon>> = MutableLiveData<ArrayList<Salon>>()
    val user: LiveData<NormalUser> = _user
    val wishlist: LiveData<ArrayList<Salon>> = _wishlist

    suspend fun getUserDetail(id: String) {
        _user.value = dbServices.getNormalUserServices()?.getUserByAccountId(id)
    }

    suspend fun getWishlist() {
        var salonList = ArrayList<Salon>()

        for (i in user.value?.wishList!!) {
            val salon = dbServices.getSalonServices()?.getSalonById(i.id)
            salonList.add(salon!!)
        }

        _wishlist.value = salonList
    }
}