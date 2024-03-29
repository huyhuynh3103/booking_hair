package com.example.hair_booking

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.model.*
import com.example.hair_booking.ui.manager.appointment.overview.ManagerAppointmentListAdapter
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.ui.admin.discount.overview.AdminDiscountListActivity
import com.example.hair_booking.ui.admin.discount.overview.AdminDiscountListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_discount.DiscountListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_salon.SalonListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_service.ServiceListAdapter
import com.example.hair_booking.ui.normal_user.booking.choose_stylist.StylistListAdapter
import com.example.hair_booking.ui.manager.stylist.StylistRecycleViewAdapter
import com.example.hair_booking.ui.admin.managers_list.ManagersListAdapter
import com.example.hair_booking.ui.admin.salon.SalonRecyclerViewAdapter
import com.example.hair_booking.ui.admin.service.overview.AdminServiceListAdapter
import com.example.hair_booking.ui.admin.users_list.UsersListAdapter
import com.example.hair_booking.ui.normal_user.history.overview.HistoryListAdapter
import com.example.hair_booking.ui.normal_user.home.SalonAdapter
import com.example.hair_booking.ui.normal_user.wishlist.WishlistRecyclerViewAdapter

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
    val adapter = recyclerView.adapter as ManagerAppointmentListAdapter
    //val adapter = recyclerView.adapter as AppointmentListAdapter
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

@BindingAdapter("managerStylistList")
fun bindManagerStylistListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Stylist>?) {
    val adapter = recyclerView.adapter as StylistRecycleViewAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("adminSalonList")
fun bindAdminSalonListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Salon>?) {
    val adapter = recyclerView.adapter as SalonRecyclerViewAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("usersListData")
fun bindUserListRecyclerView(recyclerView: RecyclerView, dataUser: ArrayList<NormalUser>?) {
    val adapter = recyclerView.adapter as UsersListAdapter
    if (dataUser != null) {
        adapter?.setData(dataUser)
        }
}

@BindingAdapter("managersListData")
fun bindManagerListRecyclerView(recyclerView: RecyclerView, dataAccount: ArrayList<Account>?) {
    val adapter = recyclerView.adapter as ManagersListAdapter
    if (dataAccount != null) {
        adapter?.setData(dataAccount)
    }

}
@BindingAdapter("list", "selected", "default")
fun setSelectedItem(spinner: Spinner, list: ArrayList<Salon>?, selected: String?, default: String?) {
    if (list != null && selected != null) {
        for (i in list?.indices!!) {
            if (list[i].id == selected) {
                spinner.setSelection(i)
            }
        }
    }
    else if (list != null && selected == null){
        for (i in list?.indices!!) {
            if (list[i].id == default) {
                spinner.setSelection(i)
            }
        }
    }
    else {
        Log.i("AdapterBindingError", "Set selected fail")
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

@BindingAdapter("list", "salon")
fun setWorkPlace(textview: TextView, list: ArrayList<Salon>?, salon: String?) {
    if (list != null && salon != null) {
        for (i in list?.indices!!) {
            if (list[i].id == salon) {
                textview.text = list[i].toString()
            }
        }
    }
    else if (list != null && salon == null){
        textview.text = list[0].toString()
    }
    else {
        Log.i("AdapterBindingError", "Set selected fail")
    }
}

@BindingAdapter("historyList")
fun bindHistoryListRecycleView(recyclerView: RecyclerView,data:ArrayList<Appointment>?){
    val adapter = recyclerView.adapter as HistoryListAdapter
    if(data!=null){
        adapter.setData(data)
    }
}
@BindingAdapter("adminServiceList")
fun bindAdminServiceListRecyclerView(recyclerView: RecyclerView, data: LiveData<ArrayList<Service>>) {
    val adapter = recyclerView.adapter as AdminServiceListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("adminDiscountList")
fun bindAdminDiscountListRecyclerView(recyclerView: RecyclerView, data: LiveData<ArrayList<Discount>>) {
    val adapter = recyclerView.adapter as AdminDiscountListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("shift")
fun setCheckedCheckBox(checkBox: CheckBox?, shift: HashMap<String, *>?) {
    if (shift != null) {
        checkBox?.isChecked = shift?.get("isWorking") == true
    }
}

@BindingAdapter("userWishlist")
fun bindNormalUserWishlistRecyclerView(recyclerView: RecyclerView, data: ArrayList<Salon>?) {
    val adapter = recyclerView.adapter as WishlistRecyclerViewAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}
