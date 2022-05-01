package com.example.hair_booking.ui.manager.appointment.overview

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.Constant
import com.example.hair_booking.databinding.ManagerAppointmentListItemBinding
import com.example.hair_booking.model.Appointment

class ManagerAppointmentListAdapter: RecyclerView.Adapter<ManagerAppointmentListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var appointmentList: LiveData<ArrayList<Appointment>>? = null
    private var appointmentToBeHiddenIndexWhenSearch: ArrayList<Int>? = null
    private var appointmentToBeHiddenIndexWhenFilter: ArrayList<Int>? = null

    fun setData(appointmentList: LiveData<ArrayList<Appointment>>) {
        this.appointmentList = appointmentList

        // The UI will be loaded before the database return the appointment list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

    fun setAppointmentToBeHiddenIndexWhenSearch(appointmentToBeHiddenIndex: ArrayList<Int>?) {
        this.appointmentToBeHiddenIndexWhenSearch = appointmentToBeHiddenIndex

        Log.d("text1231", "notify")
        notifyDataSetChanged()
    }

    fun setAppointmentToBeHiddenIndexWhenFilter(appointmentToBeHiddenIndex: ArrayList<Int>?) {
        this.appointmentToBeHiddenIndexWhenFilter = appointmentToBeHiddenIndex

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



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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

        // Show appointment
        holder.itemView.visibility = View.VISIBLE
        holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if(appointmentToBeHiddenIndexWhenSearch != null){
            if(appointmentToBeHiddenIndexWhenSearch!!.contains(position)) {
                // Hide appointment
                holder.itemView.visibility = View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
        }

        if(appointmentToBeHiddenIndexWhenFilter != null) {
            if(appointmentToBeHiddenIndexWhenFilter!!.contains(position)) {
                // Hide appointment
                holder.itemView.visibility = View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
        }

        when(appointment?.status) {
            Constant.AppointmentStatus.isPending -> {
                holder.appointmentListItemBinding.appointmentListStatus.setTextColor(Color.parseColor("#787878"))
            }
            Constant.AppointmentStatus.isCheckout -> {
                holder.appointmentListItemBinding.appointmentListStatus.setTextColor(Color.parseColor("#4CAF50"))
            }
            Constant.AppointmentStatus.isAbort -> {
                holder.appointmentListItemBinding.appointmentListStatus.setTextColor(Color.parseColor("#DD2828"))
            }
        }

    }

    override fun getItemCount(): Int {
        return appointmentList?.value?.size ?: 0
    }
}