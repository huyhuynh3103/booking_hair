package com.example.hair_booking.ui.admin.service.edit_service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModel
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Service
import com.example.hair_booking.services.db.dbServices

class AdminEditServiceViewModel: ViewModel() {

    private var _serviceToBeEditedId = MutableLiveData<String>()
    val serviceToBeEditedId: LiveData<String> = _serviceToBeEditedId

    private val _serviceTitle = MutableLiveData<String>()
    private val _serviceDescription = MutableLiveData<String>()
    private val _serviceDuration = MutableLiveData<Long>()
    private val _servicePrice = MutableLiveData<Long>()
    val serviceTitle: LiveData<String> = _serviceTitle
    val serviceDescription: LiveData<String> = _serviceDescription
    val serviceDuration: LiveData<Long> = _serviceDuration
    val servicePrice: LiveData<Long> = _servicePrice

    // Set confirm button onclick to be observable
    private val _confirmBtnClicked = MutableLiveData<Boolean>()
    val confirmBtnClicked: LiveData<Boolean> = _confirmBtnClicked
    fun onConfirmBtnClicked() {
        _confirmBtnClicked.value = true
    }

    // Check if there are empty fields
    fun checkEmptyFieldsExist(title: String, description: String, duration: String, price: String): Boolean {
        return title.isNullOrEmpty() || description.isNullOrEmpty()
                || duration.isNullOrEmpty()
                || price.isNullOrEmpty()
    }

    suspend fun prepareData(serviceId: String) {
        _serviceToBeEditedId.value = serviceId
        val service = dbServices.getServiceServices()?.getServiceById(serviceId)
        _serviceTitle.value = service?.title
        _serviceDescription.value = service?.description
        _serviceDuration.value = service?.duration
        _servicePrice.value = service?.price
    }

    suspend fun updateService(title: String, description: String, duration: String, price: String): Boolean {
        val serviceToBeSaved = hashMapOf(
            "title" to title,
            "description" to description,
            "duration" to duration.toLong(),
            "price" to price.toLong()
        )

        return dbServices.getServiceServices()!!.updateService(_serviceToBeEditedId.value!!, serviceToBeSaved)
    }

}