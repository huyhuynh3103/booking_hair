package com.example.hair_booking.ui.admin.service.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.AdminServiceListItemBinding
import com.example.hair_booking.model.Service

class AdminServiceListAdapter(private val viewModel: AdminServiceListViewModel): RecyclerView.Adapter<AdminServiceListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var serviceList: LiveData<ArrayList<Service>>? = null

    fun setData(serviceList: LiveData<ArrayList<Service>>) {
        this.serviceList = serviceList

        // The UI will be loaded before the database return the appointment list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }


    inner class ViewHolder(var serviceListItemBinding: AdminServiceListItemBinding): RecyclerView.ViewHolder(serviceListItemBinding.root) {

        init {
            // Set on item click listener
            serviceListItemBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item position as parameter
                onItemClick?.invoke(adapterPosition)
            }
            serviceListItemBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var serviceListItemBinding: AdminServiceListItemBinding = AdminServiceListItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(serviceListItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var service: Service? = serviceList?.value?.get(position) ?: null
        holder.serviceListItemBinding.service = service
        holder.serviceListItemBinding.viewModel = viewModel
    }

    override fun getItemCount(): Int {
        return serviceList?.value?.size ?: 0
    }
}