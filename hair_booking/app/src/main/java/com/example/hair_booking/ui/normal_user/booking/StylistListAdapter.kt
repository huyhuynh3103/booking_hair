package com.example.hair_booking.ui.normal_user.booking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.BookingStylistItemBinding
import com.example.hair_booking.model.Stylist

class StylistListAdapter: RecyclerView.Adapter<StylistListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private lateinit var stylistList: ArrayList<Stylist>
    fun setData(stylistList: ArrayList<Stylist>) {
        this.stylistList = stylistList
    }

    inner class ViewHolder(var bookingStylistItemBinding: BookingStylistItemBinding): RecyclerView.ViewHolder(bookingStylistItemBinding.root) {

        init {
            bookingStylistItemBinding.executePendingBindings()
//            // Set on item click listener
//            listItemView.setOnClickListener {
//                // When item clicked, invoke onItemClick function with clicked item as parameter
//                onItemClick?.invoke(adapterPosition)
//            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StylistListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var bookingStylistItemBinding: BookingStylistItemBinding = BookingStylistItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(bookingStylistItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stylist: Stylist = stylistList[position]
        holder.bookingStylistItemBinding.stylist = stylist
    }

    override fun getItemCount(): Int {
        return stylistList.size
    }
}