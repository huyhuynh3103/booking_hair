package com.example.hair_booking.ui.manager.stylist

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.DocumentReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class ManagerStylistDetailViewModel: ViewModel() {
    private val _stylist: MutableLiveData<Stylist> = MutableLiveData()
    private val _salonList: MutableLiveData<ArrayList<Salon>> = MutableLiveData()
    private val _account: MutableLiveData<Account> = MutableLiveData()
    private val _avatar: MutableLiveData<Uri> = MutableLiveData()

    val stylist: LiveData<Stylist> = _stylist
    val salonList: LiveData<ArrayList<Salon>> = _salonList
    val account: LiveData<Account> = _account
    val avatar: LiveData<Uri> = _avatar

    init {
        viewModelScope.launch {
            getSalonList()
        }
    }

    private suspend fun getSalonList() {
        dbServices.getSalonServices()?.findAll()?.observeForever {
            _salonList.value = it
        }
    }

    suspend fun getStylistDetail(context: Activity, imageView: ImageView, id: String) {
        val stylist = dbServices.getStylistServices()?.findById(id)
        _stylist.value = stylist

        // Load image from cloudinary url to image view
        Picasso.with(context).load(stylist!!.avatar).into(imageView)
    }

    suspend fun getManagerAccount(email: String) {
        _account.value = dbServices.getAccountServices()?.getManagerAccountByEmail(email)
    }

    suspend fun getSelectedWorkplace(position: Int): DocumentReference? {
        var result: DocumentReference?
        var selectedID: String? = null
        val list = salonList.value

        for (i in list?.indices!!) {
            if (i == position) {
                selectedID = list[i].id
            }
        }

        result = dbServices.getSalonServices()?.getWorkplace(selectedID)

        return result
    }

    suspend fun getShiftRef(id: String): DocumentReference? {
        return dbServices.getShiftServices()?.getShiftRef(id)
    }

    suspend fun getStylistRef(id: String): DocumentReference? {
        return dbServices.getStylistServices()?.getStylistRef(id)
    }

    suspend fun isBooked(stylistRef: DocumentReference?): Boolean {
        return dbServices.getAppointmentServices()?.countAppointment(stylistRef)!! > 0
    }

    suspend fun isBooked(stylistRef: DocumentReference?, shiftRef: DocumentReference?): Boolean {
        return dbServices.getAppointmentServices()?.countAppointment(stylistRef, shiftRef)!! > 0
    }

    suspend fun updateStylist(context: Activity, id: String, stylist: Stylist) {
        //File to upload to cloudinary
        try {
            val url = MediaManager.get()
                .upload(_avatar.value)
                .option("folder", Constant.ImagePath.stylist)
                .option("public_id", id) // use stylist id as public id
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("cloudinary", "onStart")
                    }


                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        Log.d("cloudinary", "onProgress")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                        stylist.setAvatar(resultData?.get("secure_url") as String)

                        GlobalScope.launch {
                            dbServices.getStylistServices()?.updateOne(id, stylist)
                        }


                    }

                    override fun onError(requestId: String?, error: ErrorInfo) {
                        Log.d("cloudinary", "onError")
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo) {
                        Log.d("cloudinary", "onReschdule")
                    }
                }).startNow(context)


        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    suspend fun addStylist(stylist: Stylist) {
        dbServices.getStylistServices()?.add(stylist)
    }

    suspend fun deleteStylist(id: String) {
        dbServices.getStylistServices()?.delete(id)
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
}