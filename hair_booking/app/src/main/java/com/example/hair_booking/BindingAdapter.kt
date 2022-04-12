package com.example.hair_booking

import android.util.Log
import android.widget.Spinner
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

@BindingAdapter("data")
fun bindBookingDiscountListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Discount>?) {
    val adapter = recyclerView.adapter as DiscountListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
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

@BindingAdapter("list", "selected")
fun setSelectedItem(spinner: Spinner, list: ArrayList<Salon>?, selected: String?) {
    if (list != null && selected != null) {
        for (i in list?.indices!!) {
            if (list[i].id == selected) {
                spinner.setSelection(i)
            }
        }
    }
    else {
        Log.i("AdapterBindingError", "Set selected fail")
    }
}