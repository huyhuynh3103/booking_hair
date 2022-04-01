package com.example.hair_booking

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.model.Discount
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Service
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.ui.normal_user.booking.DiscountListAdapter
import com.example.hair_booking.ui.normal_user.booking.SalonListAdapter
import com.example.hair_booking.ui.normal_user.booking.ServiceListAdapter
import com.example.hair_booking.ui.normal_user.booking.StylistListAdapter

@BindingAdapter("data")
fun bindBookingStylistListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Stylist>?) {
    val adapter = recyclerView.adapter as StylistListAdapter?
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("data")
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

@BindingAdapter("data")
fun bindBookingDiscountListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Discount>?) {
    val adapter = recyclerView.adapter as DiscountListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}