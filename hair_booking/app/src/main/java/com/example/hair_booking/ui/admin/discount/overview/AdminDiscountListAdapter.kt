package com.example.hair_booking.ui.admin.discount.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.AdminDiscountListItemBinding
import com.example.hair_booking.model.Discount

class AdminDiscountListAdapter(private val viewModel: AdminDiscountListViewModel): RecyclerView.Adapter<AdminDiscountListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var discountList: LiveData<ArrayList<Discount>>? = null

    fun setData(discountList: LiveData<ArrayList<Discount>>) {
        this.discountList = discountList

        // The UI will be loaded before the database return the appointment list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }


    inner class ViewHolder(var discountListItemBinding: AdminDiscountListItemBinding): RecyclerView.ViewHolder(discountListItemBinding.root) {

        init {
            // Set on item click listener
            discountListItemBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item position as parameter
                onItemClick?.invoke(adapterPosition)
            }
            discountListItemBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var discountListItemBinding = AdminDiscountListItemBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(discountListItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var discount = discountList?.value?.get(position) ?: null
        holder.discountListItemBinding.discount = discount
        holder.discountListItemBinding.viewModel = viewModel
    }

    override fun getItemCount(): Int {
        return discountList?.value?.size ?: 0
    }
}