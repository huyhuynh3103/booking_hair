package com.example.hair_booking.ui.normal_user.booking.choose_stylist

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.BookingStylistItemBinding
import com.example.hair_booking.model.Stylist
import com.squareup.picasso.Picasso

class StylistListAdapter: RecyclerView.Adapter<StylistListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var stylistList: ArrayList<Stylist> = ArrayList()
    fun setData(stylistList: ArrayList<Stylist>) {
        this.stylistList = stylistList

        // The UI will be loaded before the database return the stylist list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

    inner class ViewHolder(var bookingStylistItemBinding: BookingStylistItemBinding, var context: Activity): RecyclerView.ViewHolder(bookingStylistItemBinding.root) {

        init {
            // Set on item click listener
            bookingStylistItemBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item as parameter
                onItemClick?.invoke(adapterPosition)
            }
            bookingStylistItemBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var bookingStylistItemBinding: BookingStylistItemBinding = BookingStylistItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(bookingStylistItemBinding, context as Activity)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stylist: Stylist = stylistList[position]
        holder.bookingStylistItemBinding.stylist = stylist

        val stylistImageView = holder.itemView.findViewById<ImageView>(R.id.stylistAvatar)

        // Load image from cloudinary url to image view
        if(!stylist!!.avatar.isNullOrEmpty())
            Picasso.with(holder.context).load(stylist!!.avatar).into(stylistImageView)
    }

    override fun getItemCount(): Int {
        return stylistList.size
    }
}