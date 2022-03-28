package com.example.hair_booking

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.ui.normal_user.booking.StylistListAdapter

@BindingAdapter("data")
fun bindBookingStylistListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Stylist>?) {
    val adapter = recyclerView.adapter as StylistListAdapter?
    if (data != null) {
        adapter?.setData(data)
    }
}