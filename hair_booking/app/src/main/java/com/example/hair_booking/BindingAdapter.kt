package com.example.hair_booking

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.model.*
import com.example.hair_booking.ui.manager.appointment.AppointmentListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_discount.DiscountListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_salon.SalonListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_service.ServiceListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_stylist.StylistListAdapter
import com.example.hair_booking.ui.manager.stylist.StylistRecycleViewAdapter
import com.example.hair_booking.ui.normal_user.home.SalonAdapter
@BindingAdapter("data")
fun bindBookingStylistListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Stylist>?) {
    val adapter = recyclerView.adapter as StylistListAdapter?
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("bookingSalonData")
fun bindBookingSalonListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Salon>?) {
    val adapter = recyclerView.adapter as SalonListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("data")
fun bindBookingServiceListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Service>?) {
    val adapter = recyclerView.adapter as ServiceListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("discountListForBookingData")
fun bindBookingDiscountListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Discount>?) {
    val adapter = recyclerView.adapter as DiscountListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("managerAppointmentList")
fun bindManagerAppointmentListRecyclerView(recyclerView: RecyclerView, data: LiveData<ArrayList<Appointment>>) {
    val adapter = recyclerView.adapter as AppointmentListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("serviceTitle")
fun setServiceTitle(textView: TextView?, service: HashMap<String, *>?) {
    if(service?.get("title") != null)
        textView?.text = "Dịch vụ: " + service["title"] as String
    else
        textView?.text = ""
}


@BindingAdapter("data")
fun bindSalonListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Salon>?) {
    val adapter = recyclerView.adapter as SalonAdapter
    if (data != null) {


        data.forEach { salon ->
            Log.d("huy-test-bind",salon.name.toString())
            Log.d("huy-test-bind",salon.avatar.toString())
            Log.d("huy-test-bind",salon.rate.toString())
        }

        adapter.setData(data)
    }
    else
    {
        Log.d("huy-test-bind","data in binding null")
    }
}

@BindingAdapter("data")
fun bindManagerStylistListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Stylist>?) {
    val adapter = recyclerView.adapter as StylistRecycleViewAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("list", "selected")
fun setSelectedItem(spinner: Spinner, list: ArrayList<Salon>?, selected: String?) {
    if (list != null && selected != null) {
        for (i in list?.indices!!) {
            if (list[i].id == selected) {
                spinner.setSelection(i)
            }
        }
    }
    else if (list != null && selected == null){
        spinner.setSelection(0)
    }
    else {
        Log.i("AdapterBindingError", "Set selected fail")
    }
}