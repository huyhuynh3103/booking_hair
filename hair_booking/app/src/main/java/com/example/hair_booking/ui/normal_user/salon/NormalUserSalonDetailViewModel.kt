package com.example.hair_booking.ui.normal_user.salon

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.db.dbServices

class NormalUserSalonDetailViewModel(private val context: Context): ViewModel() {
    /*
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _avatar = MutableLiveData<String>()
    val avatar: LiveData<String> = _avatar

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _openHour = MutableLiveData<String>()
    val openHour: LiveData<String> = _openHour

    private val _closeHour = MutableLiveData<String>()
    val closeHour: LiveData<String> = _closeHour

    private val _rate = MutableLiveData<Long>()
    val rate: LiveData<Long> = _rate

    private val _address = MutableLiveData<HashMap<String, String>>()
    val address: LiveData<HashMap<String, String>> = _address
    */

    private val _salon: MutableLiveData<Salon> = MutableLiveData(
//        Salon("1", "MySalon", "AvatarString", "Describe something", 4, "08:00", "16:00", hashMapOf("1" to "39 Cao Lỗ, phường 4, quận 8"))
    )
    val salon: LiveData<Salon> = _salon
    private val _rate = MutableLiveData<Float>()
    val rate:LiveData<Float> = _rate
    private val _idAvatar = MutableLiveData<Int>()
    val idAvatar:LiveData<Int> = _idAvatar
    fun getSalonDetail(id: String){
        dbServices.getSalonServices()?.getSalonDetail(id)?.observeForever {
            _salon.value = it
            _rate.value = it.rate!!.toFloat()
            _idAvatar.value = getSalonAvatarResource(context)
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