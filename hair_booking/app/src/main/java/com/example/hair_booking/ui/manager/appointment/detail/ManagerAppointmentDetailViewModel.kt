package com.example.hair_booking.ui.manager.appointment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.services.local.SalonServices
import com.google.firebase.firestore.DocumentReference

class ManagerAppointmentDetailViewModel : ViewModel() {
    private val _appointmentId: MutableLiveData<String> = MutableLiveData<String>()
    val appointmentId: LiveData<String> = _appointmentId

    private val _hairSalonName: MutableLiveData<String> = MutableLiveData<String>()
    val hairSalonName: LiveData<String> = _hairSalonName

    private val _hairSalonAddress: MutableLiveData<String> = MutableLiveData<String>()
    val hairSalonAddress: LiveData<String> = _hairSalonAddress

    private val _hairSalonPhoneNumber: MutableLiveData<String> = MutableLiveData<String>()
    val hairSalonPhoneNumber: LiveData<String> = _hairSalonPhoneNumber

    private val _serviceTitle: MutableLiveData<String> = MutableLiveData<String>()
    val serviceTitle: LiveData<String> = _serviceTitle

    private val _stylistFullName: MutableLiveData<String> = MutableLiveData<String>()
    val stylistFullName: LiveData<String> = _stylistFullName

    private val _bookingDate: MutableLiveData<String> = MutableLiveData<String>()
    val bookingDate: LiveData<String> = _bookingDate

    private val _bookingDateTime: MutableLiveData<String> = MutableLiveData<String>()
    val bookingDateTime: LiveData<String> = _bookingDateTime

    private val _discountTitle: MutableLiveData<String> = MutableLiveData<String>()
    val discountTitle: LiveData<String> = _discountTitle

    private val _note: MutableLiveData<String> = MutableLiveData<String>()
    val note: LiveData<String> = _note

    private val _servicePrice: MutableLiveData<String> = MutableLiveData<String>()
    val servicePrice: LiveData<String> = _servicePrice

    private val _discountPrice: MutableLiveData<String> = MutableLiveData<String>()
    val discountPrice: LiveData<String> = _discountPrice

    private val _totalPrice: MutableLiveData<String> = MutableLiveData<String>()
    val totalPrice: LiveData<String> = _totalPrice

    private val _status: MutableLiveData<String> = MutableLiveData<String>()
    val status: LiveData<String> = _status

    // Set edit button onclick to be observable
    private val _editBtnClicked = MutableLiveData<Boolean>()
    val editBtnClicked: LiveData<Boolean> = _editBtnClicked
    fun onEditBtnClicked() {
        _editBtnClicked.value = true
    }

    // Set cancel button onclick to be observable
    private val _cancelBtnClicked = MutableLiveData<Boolean>()
    val cancelBtnClicked: LiveData<Boolean> = _cancelBtnClicked
    fun onCancelBtnClicked() {
        _cancelBtnClicked.value = true
    }

    // Set checkout btn onclick to be observable
    private val _checkOutBtnClicked = MutableLiveData<Boolean>()
    val checkOutBtnClicked: LiveData<Boolean> = _checkOutBtnClicked
    fun onCheckOutBtnClicked() {
        _checkOutBtnClicked.value = true
    }


    suspend fun prepareAppointmentDetail(appointmentId: String) {
        _appointmentId.postValue(appointmentId)
        val appointment: Appointment? = dbServices.getAppointmentServices()!!.getAppointmentById(appointmentId)
        _status.postValue(appointment?.status!!.uppercase())
        _hairSalonName.postValue(appointment?.hairSalon?.get("name") as String?)
        _hairSalonAddress.postValue(SalonServices.addressToString(appointment?.hairSalon?.get("address") as HashMap<String, *>))
        _hairSalonPhoneNumber.postValue(appointment.hairSalon?.get("phoneNumber") as String?)
        _serviceTitle.postValue((appointment.service?.get("title") as String?) + " - " + ((appointment.service?.get("duration") as Long).toString()) + " phút")
        _stylistFullName.postValue("Stylist " + appointment.stylist?.get("fullName") as String?)
        _bookingDate.postValue(appointment.bookingDate!!)
        var bookingTime: String = appointment.bookingTime!!
        bookingTime = bookingTime.replace('.', 'h')
        _bookingDateTime.postValue("$bookingTime Ngày ${appointment.bookingDate!!}")

        if((appointment.discountApplied?.get("title") as String?).isNullOrEmpty())
            _discountTitle.postValue("Không có khuyến mãi")
        else
            _discountTitle.postValue(appointment.discountApplied?.get("title") as String?)


        if(appointment.note.isNullOrEmpty())
            _note.postValue("Không có ghi chú")
        else
            _note.postValue(appointment.note)
        _totalPrice.postValue((appointment.totalPrice).toString())

        val service = dbServices.getServiceServices()!!.getServiceById((appointment.service?.get("id") as DocumentReference).id)
        _servicePrice.postValue(service!!.price.toString())
        _discountPrice.postValue((service.price!!.minus(appointment.totalPrice!!)).toString())


    }

    suspend fun updateAppointmentStatus(appointmentStatus: String): Boolean? {
        _status.postValue(appointmentStatus.uppercase())
        return dbServices.getAppointmentServices()!!.updateAppointmentStatus(_appointmentId.value!!, appointmentStatus)
    }

    fun setAppointmentId(appointmentId: String) {
        this._appointmentId.postValue(appointmentId)
    }

}