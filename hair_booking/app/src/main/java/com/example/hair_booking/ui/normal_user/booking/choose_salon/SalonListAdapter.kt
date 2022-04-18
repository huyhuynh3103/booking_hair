package com.example.hair_booking.ui.normal_user.booking.choose_salon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.BookingSalonItemBinding
import com.example.hair_booking.model.Salon

class SalonListAdapter: RecyclerView.Adapter<SalonListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var salonList: ArrayList<Salon> = ArrayList()
    fun setData(salonList: ArrayList<Salon>) {
        this.salonList = salonList

        // The UI will be loaded before the database return the salon list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

    inner class ViewHolder(var bookingSalonItemBinding: BookingSalonItemBinding): RecyclerView.ViewHolder(bookingSalonItemBinding.root) {

        init {
            // Set on item click listener
            bookingSalonItemBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item position as parameter
                onItemClick?.invoke(adapterPosition)
            }
            bookingSalonItemBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var bookingSalonItemBinding: BookingSalonItemBinding = BookingSalonItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(bookingSalonItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val salon: Salon = salonList[position]
        holder.bookingSalonItemBinding.salon = salon
    }

    override fun getItemCount(): Int {
        return salonList.size
    }
}