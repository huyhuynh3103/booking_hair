package com.example.hair_booking.ui.normal_user.booking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.BookingServiceItemBinding
import com.example.hair_booking.model.Service

class ServiceListAdapter: RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private lateinit var serviceList: ArrayList<Service>
    fun setData(serviceList: ArrayList<Service>) {
        this.serviceList = serviceList
    }

    inner class ViewHolder(var bookingServiceItemBinding: BookingServiceItemBinding): RecyclerView.ViewHolder(bookingServiceItemBinding.root) {

        init {
            bookingServiceItemBinding.executePendingBindings()
//            // Set on item click listener
//            listItemView.setOnClickListener {
//                // When item clicked, invoke onItemClick function with clicked item as parameter
//                onItemClick?.invoke(adapterPosition)
//            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var bookingServiceItemBinding: BookingServiceItemBinding = BookingServiceItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(bookingServiceItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service: Service = serviceList[position]
        holder.bookingServiceItemBinding.service = service
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }
}