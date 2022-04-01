package com.example.hair_booking

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.model.Salon
import com.example.hair_booking.ui.normal_user.home.SalonAdapter

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
