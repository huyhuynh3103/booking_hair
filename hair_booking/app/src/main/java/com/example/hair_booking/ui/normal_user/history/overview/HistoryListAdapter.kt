package com.example.hair_booking.ui.normal_user.history.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.HistoryBookingItemBinding
import com.example.hair_booking.model.Appointment

class HistoryListAdapter:RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {
    var onItemClick: ((id: String) -> Unit)? = null
    var onCancleClick: ((position:Int,subId:String?) -> Unit)? = null
    private var historyList: ArrayList<Appointment> = ArrayList()
    fun setData(historyList: ArrayList<Appointment>) {
        this.historyList = historyList

        // The UI will be loaded before the database return the discount list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

    inner class ViewHolder(var historyBookingItemBinding: HistoryBookingItemBinding): RecyclerView.ViewHolder(historyBookingItemBinding.root) {

        init {
            // Set on item click listener
            historyBookingItemBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item position as parameter
                onItemClick?.invoke(historyList[absoluteAdapterPosition].id)
            }
            historyBookingItemBinding.CancleButton.setOnClickListener {
                onCancleClick?.invoke(absoluteAdapterPosition,historyList[absoluteAdapterPosition].appointmentSubId)
            }
            historyBookingItemBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var historyBookingItemBinding: HistoryBookingItemBinding = HistoryBookingItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(historyBookingItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment: Appointment = historyList[position]
        val addressSalon = appointment.hairSalon?.get("address") as HashMap<String,*>?
        val streetNumber:String? = addressSalon?.get("streetNumber") as String?
        val streetName:String? = addressSalon?.get("streetName") as String?
        val ward:String? = addressSalon?.get("ward") as String?
        val district:String? = addressSalon?.get("district") as String?
        val city:String? = addressSalon?.get("city") as String?
        var address = ""
        if(streetNumber!=null){
            address+=streetNumber.toString()
        }
        if(streetName!=null){
            address+= " $streetName"
        }
        if(ward!=null){
            address+=", phường $ward"
        }
        if(district!=null){
            address+=", quận $district"
        }
        if(city!=null){
            address+=", tp $city"
        }
        //val address = "$streetNumber $streetName, phường $ward, quận $district, tp $city"
        holder.historyBookingItemBinding.appointment = appointment
        holder.historyBookingItemBinding.address = address
        holder.historyBookingItemBinding.isPending = Constant.AppointmentStatus.isPending
        holder.historyBookingItemBinding.isCheckOut = Constant.AppointmentStatus.isCheckout
        holder.historyBookingItemBinding.isAbort = Constant.AppointmentStatus.isAbort
        holder.historyBookingItemBinding.rate = appointment.rate!!.toFloat()
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}