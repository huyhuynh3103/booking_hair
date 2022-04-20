package com.example.hair_booking.ui.normal_user.booking.choose_discount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.BookingDiscountItemBinding
import com.example.hair_booking.model.Discount

class DiscountListAdapter: RecyclerView.Adapter<DiscountListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var discountList: ArrayList<Discount> = ArrayList()
    fun setData(discountList: ArrayList<Discount>) {
        this.discountList = discountList

        // The UI will be loaded before the database return the discount list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

    inner class ViewHolder(var bookingDiscountItemBinding: BookingDiscountItemBinding): RecyclerView.ViewHolder(bookingDiscountItemBinding.root) {

        init {
            // Set on item click listener
            bookingDiscountItemBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item position as parameter
                onItemClick?.invoke(adapterPosition)
            }
            bookingDiscountItemBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var bookingDiscountItemBinding: BookingDiscountItemBinding = BookingDiscountItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(bookingDiscountItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val discount: Discount = discountList[position]
        holder.bookingDiscountItemBinding.discount = discount
    }

    override fun getItemCount(): Int {
        return discountList.size
    }
}