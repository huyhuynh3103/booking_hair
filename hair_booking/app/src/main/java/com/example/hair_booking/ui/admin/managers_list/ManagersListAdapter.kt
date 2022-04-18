package com.example.hair_booking.ui.admin.managers_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.ItemManagerListBinding
import com.example.hair_booking.model.Account

class ManagersListAdapter: RecyclerView.Adapter<ManagersListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var managerList: ArrayList<Account> = ArrayList()
    fun setData(managerList: ArrayList<Account>) {
        this.managerList = managerList

        // The UI will be loaded before the database return the salon list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

    inner class ViewHolder(var itemManagerListBinding: ItemManagerListBinding): RecyclerView.ViewHolder(itemManagerListBinding.root) {

        init {
            // Set on item click listener
            itemManagerListBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item position as parameter
                onItemClick?.invoke(adapterPosition)
            }
            itemManagerListBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagersListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var itemManagerListBinding: ItemManagerListBinding = ItemManagerListBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(itemManagerListBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val manager: Account = managerList[position]
        holder.itemManagerListBinding.manager = manager
    }

    override fun getItemCount(): Int {
        return managerList.size
    }
}