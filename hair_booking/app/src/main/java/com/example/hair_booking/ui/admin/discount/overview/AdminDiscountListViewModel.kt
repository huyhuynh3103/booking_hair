package com.example.hair_booking.ui.admin.discount.overview

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Discount
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AdminDiscountListViewModel: ViewModel() {

    private var _discountList = MutableLiveData<ArrayList<Discount>>()
    val discountList: LiveData<ArrayList<Discount>> = _discountList

    private var _discountToBeEditedId = MutableLiveData<String>()
    val discountToBeEditedId: LiveData<String> = _discountToBeEditedId

    private var _discountToBeDeleteId = MutableLiveData<String>()
    val discountToBeDeleteId: LiveData<String> = _discountToBeDeleteId

    // Set add button onclick to be observable
    private val _addNewDiscountBtnClicked = MutableLiveData<Boolean>()
    val addNewDiscountBtnClicked: LiveData<Boolean> = _addNewDiscountBtnClicked
    fun onAddNewDiscountBtnClicked() {
        _addNewDiscountBtnClicked.value = true
    }

    // Set edit button onclick to be observable
    private val _editDiscountBtnClicked = MutableLiveData<Boolean>()
    val editDiscountBtnClicked: LiveData<Boolean> = _editDiscountBtnClicked
    fun onEditDiscountBtnClicked(discountId: String) {
        _discountToBeEditedId.value = discountId
        _editDiscountBtnClicked.value = true
    }

    // Set delete button onclick to be observable
    private val _deleteDiscountBtnClicked = MutableLiveData<Boolean>()
    val deleteDiscountBtnClicked: LiveData<Boolean> = _deleteDiscountBtnClicked
    fun onDeleteDiscountBtnClicked(discountId: String) {
        _discountToBeDeleteId.value = discountId
        _deleteDiscountBtnClicked.value = true
    }

    init {
        viewModelScope.launch {
            getDiscountList()
        }
    }



    private fun getDiscountList() {
        dbServices.getDiscountServices()!!.getDiscountListForAdmin(_discountList)
    }

    suspend fun deleteDiscount(): Boolean {
        return dbServices.getDiscountServices()!!.deleteDiscount(_discountToBeDeleteId.value!!)
    }
}