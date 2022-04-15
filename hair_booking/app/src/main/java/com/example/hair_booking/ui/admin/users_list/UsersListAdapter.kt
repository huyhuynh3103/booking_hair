package com.example.hair_booking.ui.admin.users_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.databinding.ItemUserListBinding
import com.example.hair_booking.model.NormalUser

class UsersListAdapter: RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {


    var onItemClick: ((position: Int) -> Unit)? = null

    private var userList: ArrayList<NormalUser> = ArrayList()
    fun setData(userList: ArrayList<NormalUser>) {
        this.userList = userList

        // The UI will be loaded before the database return the salon list
        // => need notify data set changed to tell the UI that the data is ready
        notifyDataSetChanged()
    }

    inner class ViewHolder(var itemUserListBinding: ItemUserListBinding): RecyclerView.ViewHolder(itemUserListBinding.root) {

        init {
            // Set on item click listener
            itemUserListBinding.root.setOnClickListener {
                // When item clicked, invoke onItemClick function with clicked item position as parameter
                onItemClick?.invoke(adapterPosition)
            }
            itemUserListBinding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the binding of custom layout based on view type
        var itemUserListBinding: ItemUserListBinding = ItemUserListBinding.inflate(inflater, parent, false)


        // Return a new holder instance with the binding of the custom layout as parameter
        // The binding of the custom layout as parameter will be used to binding view from xml layout
        return ViewHolder(itemUserListBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: NormalUser = userList[position]
        holder.itemUserListBinding.user = user
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}