package com.example.hair_booking.ui.admin.salon

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class AdminSalonDetailViewModel: ViewModel() {
    private val _salon: MutableLiveData<Salon> = MutableLiveData()
    private val _rate = MutableLiveData<Float>()
    private val _avatar: MutableLiveData<Uri> = MutableLiveData()
    val salon: LiveData<Salon> = _salon
    val rate: LiveData<Float> = _rate
    val avatar: LiveData<Uri> = _avatar

    private val _isVisibleProgressBar = MutableLiveData<Boolean>()
    val isVisibleProgressBar:LiveData<Boolean> = _isVisibleProgressBar

    init {
        // Re-init avatar uri to null, which means the user hasn't change the avatar yet
        _avatar.value = null
        _isVisibleProgressBar.value = false // hide progress bar
    }

    suspend fun getSalonDetail(context: Activity, imageView: ImageView, id: String) {
        val salon = dbServices.getSalonServices()?.getSalonById(id)
        _salon.value = salon
        _rate.value = this.salon.value?.rate!!.toFloat()

        // Load image from cloudinary url to image view
        if(!salon!!.avatar.isNullOrEmpty())
            Picasso.with(context).load(salon!!.avatar).into(imageView)
//        dbServices.getSalonServices()?.getSalonDetail(id)?.observeForever {
//            _salon.value = it
//            _rate.value = it.rate!!.toFloat()
//        }
    }

    suspend fun updateSalon(id: String, salon: Salon) {
        dbServices.getSalonServices()?.updateOne(id, salon)
    }

    suspend fun addSalon(salon: Salon) {
        dbServices.getSalonServices()?.add(salon)
    }

    suspend fun deleteSalon(id: String) {
        dbServices.getSalonServices()?.delete(id)
    }

    // Set change avatar button onclick to be observable
    private val _changeAvatarBtnClicked = MutableLiveData<Boolean>()
    val changeAvatarBtnClicked: LiveData<Boolean> = _changeAvatarBtnClicked
    fun onChangeAvatarBtnClicked() {
        _changeAvatarBtnClicked.value = true
    }

    fun setAvatarUri(uri: Uri) {
        _avatar.value = uri

    }

    fun toggleProgressBar() {
        if(_isVisibleProgressBar.value != null) {
            if(_isVisibleProgressBar.value == true)
                _isVisibleProgressBar.value = false
            else
                _isVisibleProgressBar.value = true
        }
    }
}