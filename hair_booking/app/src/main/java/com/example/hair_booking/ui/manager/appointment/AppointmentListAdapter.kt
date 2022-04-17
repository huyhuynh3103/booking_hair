package com.example.hair_booking.ui.manager.appointment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.Constant
import com.example.hair_booking.databinding.ManagerAppointmentListItemBinding
import com.example.hair_booking.model.Appointment

class AppointmentListAdapter: RecyclerView.Adapter<AppointmentListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var appointmentList: LiveData<ArrayList<Appointment>>? = null
    fun setData(appointmentList: LiveData<ArrayList<Appointment>>) {
        this.appointmentList = appointmentList

        // The UI will be loaded before the database return the appointment list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

    inner class ViewHolder(var appointmentListItemBinding: ManagerAppointmentListItemBinding): RecyclerView.ViewHolder(appointmentListItemBinding.root) {

        init {
            // Set on item click listener
            appointmentListItemBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item position as parameter
                onItemClick?.invoke(adapterPosition)
            }
            appointmentListItemBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var appointmentListItemBinding: ManagerAppointmentListItemBinding = ManagerAppointmentListItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(appointmentListItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var appointment: Appointment? = appointmentList?.value?.get(position) ?: null
        appointment?.prepareBookingTimeForDisplay()
        holder.appointmentListItemBinding.appointment = appointment
        if(appointment?.status == Constant.AppointmentStatus.accept) {
            holder.appointmentListItemBinding.appointmentListStatus.setTextColor(Color.parseColor("#4CAF50"))
        }
        else
            holder.appointmentListItemBinding.appointmentListStatus.setTextColor(Color.parseColor("#DD2828"))
    }

    override fun getItemCount(): Int {
        return appointmentList?.value?.size ?: 0
    }
}