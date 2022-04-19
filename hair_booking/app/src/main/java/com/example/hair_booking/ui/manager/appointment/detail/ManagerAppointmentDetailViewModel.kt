package com.example.hair_booking.ui.manager.appointment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.services.local.SalonServices
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch

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

    suspend fun prepareAppointmentDetail() {
        viewModelScope.launch {
            val appointment: Appointment? = dbServices.getAppointmentServices()!!.getAppointmentById(_appointmentId.value!!)
            _hairSalonName.postValue(appointment?.hairSalon?.get("name") as String?)
            _hairSalonAddress.postValue(SalonServices.addressToString(appointment?.hairSalon?.get("address") as HashMap<String, *>))
            _hairSalonPhoneNumber.postValue(appointment.hairSalon?.get("phoneNumber") as String?)
            _serviceTitle.postValue((appointment.service?.get("title") as String?) + " - " + ((appointment.service?.get("duration") as Long).toString()) + " phút")
            _stylistFullName.postValue("Stylist " + appointment.stylist?.get("fullName") as String?)
            val bookingDate: String = appointment.bookingDate!!
            var bookingTime: String = appointment.bookingTime!!
            bookingTime = bookingTime.replace('.', 'h')
            _bookingDateTime.postValue("$bookingTime Ngày $bookingDate")

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
    }

    fun setAppointmentId(appointmentId: String) {
        this._appointmentId.postValue(appointmentId)
    }


}