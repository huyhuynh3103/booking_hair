package com.example.hair_booking.ui.normal_user.salon

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference

class NormalUserSalonDetailViewModel(private val context: Context): ViewModel() {
    private val _salon: MutableLiveData<Salon> = MutableLiveData()
    val salon: LiveData<Salon> = _salon
    private val _rate = MutableLiveData<Float>()
    val rate:LiveData<Float> = _rate
    private val _idAvatar = MutableLiveData<Int>()
    val idAvatar:LiveData<Int> = _idAvatar

    private val _account: MutableLiveData<Account> = MutableLiveData()
    private val _user: MutableLiveData<NormalUser> = MutableLiveData()
    private val _wishlist: MutableLiveData<ArrayList<Salon>> = MutableLiveData<ArrayList<Salon>>()
    val account: LiveData<Account> = _account
    val user: LiveData<NormalUser> = _user
    val wishlist: LiveData<ArrayList<Salon>> = _wishlist

    fun getSalonDetail(id: String) {
        dbServices.getSalonServices()?.getSalonDetail(id)?.observeForever {
            _salon.value = it
            _rate.value = it.rate!!.toFloat()
            _idAvatar.value = getSalonAvatarResource(context)
        }
    }

//    private fun getSalonAvatarResource(context:Context): Int {
//        // get context
//        //remove extension part of avatar
//        var avatar = _salon.value?.avatar
//        if(avatar!=null){
//            val haveExtension = avatar.contains(".")
//            if(haveExtension){
//                val indexDot = avatar.indexOf(".")
//                avatar = avatar.filterIndexed { index, c -> index < indexDot   }
//            }
//        }
//        else{
//            avatar = Constant.notFoundImg
//        }
//        // get id of drawable
//        var id = context.resources.getIdentifier(avatar,"drawable",context.packageName)
//        Log.d("huy-test-avatar-id",id.toString())
//        if(id==0){
//            id = context.resources.getIdentifier(Constant.notFoundImg,"drawable",context.packageName)
//        }
//        return id
//    }

    suspend fun getUserAccount(email: String) {
        _account.value = dbServices.getAccountServices()?.getUserAccountByEmail(email)
    }

    suspend fun getUserDetail() {
        _user.value = dbServices.getNormalUserServices()?.getUserByAccountId(account.value?.id!!)
    }

    suspend fun getWishlist() {
        var salonList = ArrayList<Salon>()

        for (i in user.value?.wishList!!) {
            val salon = dbServices.getSalonServices()?.getSalonById(i.id)
            salonList.add(salon!!)
        }

        _wishlist.value = salonList
    }

    suspend fun getSalonRef(salonID: String): DocumentReference? {
        return dbServices.getSalonServices()?.getWorkplace(salonID)
    }

    suspend fun isInWishlist(salonID: String): Boolean {
        return user.value?.wishList!!.contains(getSalonRef(salonID))
    }

    suspend fun addToWishlist(userID: String, salonID: String) {
        dbServices.getNormalUserServices()?.addToWishlist(userID, getSalonRef(salonID)!!)
    }

    suspend fun removeFromWishlist(userID: String, salonID: String) {
        dbServices.getNormalUserServices()?.removeFromWishlist(userID, getSalonRef(salonID)!!)
    }

    suspend fun updateWishlist(userID: String, salonID: String) {
        if (!isInWishlist(salonID)) {
            dbServices.getNormalUserServices()?.addToWishlist(userID, getSalonRef(salonID)!!)
        }
        else {
            dbServices.getNormalUserServices()?.removeFromWishlist(userID, getSalonRef(salonID)!!)
        }
    }

    fun getSalonAvatarResource(context:Context):Int{
        // get context
        //remove extension part of avatar
        var avatar = _salon.value?.avatar
        if(avatar!=null){
            val haveExtension = avatar.contains(".")
            if(haveExtension){
                val indexDot = avatar.indexOf(".")
                avatar = avatar.filterIndexed { index, c -> index < indexDot   }
            }
        }
        else{
            avatar = Constant.notFoundImg
        }
        // get id of drawable
        var id = context.resources.getIdentifier(avatar,"drawable",context.packageName)
        Log.d("huy-test-avatar-id",id.toString())
        if(id==0){
            id = context.resources.getIdentifier(Constant.notFoundImg,"drawable",context.packageName)
        }
        return id
    }
}