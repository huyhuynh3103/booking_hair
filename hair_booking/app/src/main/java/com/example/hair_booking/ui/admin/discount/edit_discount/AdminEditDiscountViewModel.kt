package com.example.hair_booking.ui.admin.discount.edit_discount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Service
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AdminEditDiscountViewModel: ViewModel() {

    private val _discountId = MutableLiveData<String>()
    private val _discountTitle = MutableLiveData<String>()
    private val _discountDescription = MutableLiveData<String>()
    private val _discountRequiredPoint = MutableLiveData<Long>()
    private val _discountPercent = MutableLiveData<Float>()
    private val _discountDateApplied = MutableLiveData<String>()
    private val _discountDateExpired = MutableLiveData<String>()
    private val _discountServiceApplied = MutableLiveData<String>()
    private val _discountServiceAppliedId = MutableLiveData<String>()
    private val _serviceList = MutableLiveData<ArrayList<Service>>()
    private val _serviceTitleList = MutableLiveData<ArrayList<String>>()

    val discountId: LiveData<String> = _discountId
    val discountTitle: LiveData<String> = _discountTitle
    val discountDescription: LiveData<String> = _discountDescription
    val discountRequiredPoint: LiveData<Long> = _discountRequiredPoint
    val discountPercent: LiveData<Float> = _discountPercent
    val discountDateApplied: LiveData<String> = _discountDateApplied
    val discountDateExpired: LiveData<String> = _discountDateExpired
    val discountServiceApplied: LiveData<String> = _discountServiceApplied
    val discountServiceAppliedId: LiveData<String> = _discountServiceAppliedId
    val serviceList: LiveData<ArrayList<Service>> = _serviceList
    val serviceTitleList: LiveData<ArrayList<String>> = _serviceTitleList

    init {
        viewModelScope.launch {
            async {
                getServiceList()
            }.await()

            // Extract titles only
            val titleList = _serviceList.value!!.map {
                it.title!!
            }
            _serviceTitleList.value = ArrayList(titleList)
        }
    }

    // Set confirm button onclick to be observable
    private val _confirmBtnClicked = MutableLiveData<Boolean>()
    val confirmBtnClicked: LiveData<Boolean> = _confirmBtnClicked
    fun onConfirmBtnClicked() {
        _confirmBtnClicked.value = true
    }

    // Set confirm button onclick to be observable
    private val _dateAppliedEditTextClicked = MutableLiveData<Boolean>()
    val dateAppliedEditTextClicked: LiveData<Boolean> = _dateAppliedEditTextClicked
    fun onDateAppliedEditTextClicked() {
        _dateAppliedEditTextClicked.value = true
    }

    // Set confirm button onclick to be observable
    private val _dateExpiredEditTextClicked = MutableLiveData<Boolean>()
    val dateExpiredEditTextClicked: LiveData<Boolean> = _dateExpiredEditTextClicked
    fun onDateExpiredEditTextClicked() {
        _dateExpiredEditTextClicked.value = true
    }


    fun setChosenDateApplied(dateChosen: String) {
        _discountDateApplied.value = dateChosen
    }

    fun setChosenDateExpired(dateChosen: String) {
        _discountDateExpired.value = dateChosen
    }

    fun setChosenService(serviceChosenId: String, serviceChosenTitle: String) {
        _discountServiceApplied.value = serviceChosenTitle
        _discountServiceAppliedId.value = serviceChosenId
    }

    // Check if there are empty fields
    fun checkEmptyFieldsExist(inputs: HashMap<String, *>): Boolean {


        val title = inputs["title"] as String
        val description = inputs["description"] as String
        val requiredPoint = inputs["requiredPoint"].toString()
        val percent = inputs["percent"].toString()
        val dateApplied = inputs["dateApplied"] as String
        val dateExpired = inputs["dateExpired"] as String
        val serviceApplied = inputs["serviceApplied"] as String
        return title.isNullOrEmpty() || description.isNullOrEmpty()
                || requiredPoint.isNullOrEmpty() || percent.isNullOrEmpty()
                || dateApplied.isNullOrEmpty() || dateExpired.isNullOrEmpty() || serviceApplied.isNullOrEmpty()
    }

    suspend fun prepareData(discountId: String) {
        _discountId.value = discountId
        val discount = dbServices.getDiscountServices()!!.getDiscountsById(discountId)
        _discountTitle.value = discount?.title
        _discountDescription.value = discount?.description
        _discountRequiredPoint.value = discount?.requiredPoint
        _discountPercent.value = discount?.percent!!.toFloat()
        _discountDateApplied.value = discount?.dateApplied
        _discountDateExpired.value = discount?.dateExpired

        val serviceApplied = dbServices.getServiceServices()!!.getServiceById(discount?.serviceApplied!!.id)
        _discountServiceApplied.value = serviceApplied?.title
        _discountServiceAppliedId.value = serviceApplied?.id
    }

    private suspend fun getServiceList() {
        _serviceList.value = dbServices.getServiceServices()!!.findAll()
    }

    suspend fun updateDiscount(discountToBeSaved: HashMap<String, *>): Boolean {

        return dbServices.getDiscountServices()!!.updateDiscount(_discountId.value!!, discountToBeSaved)
    }

}