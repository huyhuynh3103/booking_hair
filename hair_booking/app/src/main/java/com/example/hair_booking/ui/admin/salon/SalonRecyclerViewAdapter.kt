package com.example.hair_booking.ui.admin.salon

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ItemAdminSalonBinding
import com.example.hair_booking.model.Salon
import com.squareup.picasso.Picasso

class SalonRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var salonList: ArrayList<Salon> = ArrayList()
    var onItemClick: ((Salon) -> Unit)? = null

    inner class ViewHolder(var binding: ItemAdminSalonBinding, var context: Activity): RecyclerView.ViewHolder(binding.root) {

        init {
            // Set on item click listener
            binding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item as parameter
                onItemClick?.invoke(salonList[adapterPosition])
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalonRecyclerViewAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var binding: ItemAdminSalonBinding = ItemAdminSalonBinding.inflate(inflater, parent, false)

        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(binding, context as Activity)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder: SalonRecyclerViewAdapter.ViewHolder = viewHolder as ViewHolder

        // Get the data model based on position
        val salon: Salon = salonList[position]

        holder.binding.salon = salon
        holder.binding.rating = salon.rate!!.toFloat()

        val salonImageView = holder.itemView.findViewById<ImageView>(R.id.iv_salon_avatar_item)
        // Load image from cloudinary url to image view
        if(!salon!!.avatar.isNullOrEmpty())
            Picasso.with(holder.context).load(salon!!.avatar).into(salonImageView)
    }

    override fun getItemCount(): Int {
        return salonList.size
    }

    fun setData(salonList: ArrayList<Salon>) {
        this.salonList = salonList

        // The UI will be loaded before the database return the salon list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }
}