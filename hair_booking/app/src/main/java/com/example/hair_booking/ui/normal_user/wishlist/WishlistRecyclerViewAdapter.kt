package com.example.hair_booking.ui.normal_user.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.ItemManagerStylistBinding
import com.example.hair_booking.databinding.ItemNormalUserWishlistBinding
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.ui.manager.stylist.StylistRecycleViewAdapter

class WishlistRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var wishlist: ArrayList<Salon> = ArrayList()
    var onItemClick: ((Salon) -> Unit)? = null

    inner class ViewHolder(var binding: ItemNormalUserWishlistBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            // Set on item click listener
            binding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item as parameter
                onItemClick?.invoke(wishlist[adapterPosition])
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var binding: ItemNormalUserWishlistBinding = ItemNormalUserWishlistBinding.inflate(inflater, parent, false)

        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder: WishlistRecyclerViewAdapter.ViewHolder = viewHolder as WishlistRecyclerViewAdapter.ViewHolder

        // Get the data model based on position
        val salon: Salon = wishlist[position]

        holder.binding.salon = salon
        holder.binding.rating = salon.rate!!.toFloat()
    }

    override fun getItemCount(): Int {
        return wishlist.size
    }

    fun setData(wishlist: ArrayList<Salon>) {
        this.wishlist = wishlist

        // The UI will be loaded before the database return the stylist list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

}