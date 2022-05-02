package com.example.hair_booking.ui.manager.stylist

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.BookingStylistItemBinding
import com.example.hair_booking.databinding.ItemManagerStylistBinding
import com.example.hair_booking.model.Stylist
import com.squareup.picasso.Picasso

class StylistRecycleViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var stylistList: ArrayList<Stylist> = ArrayList()
    var onItemClick: ((Stylist) -> Unit)? = null

    inner class ViewHolder(var binding: ItemManagerStylistBinding, var context: Activity): RecyclerView.ViewHolder(binding.root) {

        init {
            // Set on item click listener
            binding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item as parameter
                onItemClick?.invoke(stylistList[adapterPosition])
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StylistRecycleViewAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var binding: ItemManagerStylistBinding = ItemManagerStylistBinding.inflate(inflater, parent, false)

        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(binding, context as Activity)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder: StylistRecycleViewAdapter.ViewHolder = viewHolder as ViewHolder

        // Get the data model based on position
        val stylist: Stylist = stylistList[position]
        holder.binding.stylist = stylist
        val stylistImageView = holder.itemView.findViewById<ImageView>(R.id.iv_stylist_avatar_item)

        // Load image from cloudinary url to image view
        if(!stylist!!.avatar.isNullOrEmpty())
            Picasso.with(holder.context).load(stylist!!.avatar).into(stylistImageView)
    }

    override fun getItemCount(): Int {
        return stylistList.size
    }

    fun setData(stylistList: ArrayList<Stylist>) {
        this.stylistList = stylistList

        // The UI will be loaded before the database return the stylist list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }
}