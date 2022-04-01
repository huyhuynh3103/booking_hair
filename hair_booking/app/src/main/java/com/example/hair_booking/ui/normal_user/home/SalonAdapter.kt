package com.example.hair_booking.ui.normal_user.home

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.SalonItemUserBinding
import com.example.hair_booking.model.Salon

class SalonAdapter:RecyclerView.Adapter<SalonAdapter.ViewHolder>() {
    private var data:ArrayList<Salon> = arrayListOf()
    var onItemClick: ((Salon) -> Unit)? = null
    inner class ViewHolder(var salonItemUserBinding: SalonItemUserBinding):RecyclerView.ViewHolder(salonItemUserBinding.root){
        init {
            salonItemUserBinding.root.setOnClickListener{
                onItemClick?.invoke(data[adapterPosition])
            }
            salonItemUserBinding.executePendingBindings()
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:ArrayList<Salon>){
        if (list.isEmpty()){
            Log.d("huy-test-set","setData list is empty")
        }
        else{
            Log.d("huy-test-set","setData function invoke")
            list.forEach { salon ->
                Log.d("huy-test-set",salon.name.toString())
                Log.d("huy-test-set",salon.avatar.toString())
                Log.d("huy-test-set",salon.rate.toString())
            }
            data = list

            notifyDataSetChanged()
            }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalonAdapter.ViewHolder {
        val context = parent.context
        val inflater =LayoutInflater.from(context)

        val salonItemUserBinding: SalonItemUserBinding = SalonItemUserBinding.inflate(inflater,parent,false)

        return ViewHolder(salonItemUserBinding)
    }

    override fun onBindViewHolder(holder: SalonAdapter.ViewHolder, position: Int) {
        val salon = data[position]
        holder.salonItemUserBinding.salon = salon

        holder.salonItemUserBinding.salonItemImg.setImageDrawable(Drawable.createFromPath(salon.avatar.toString()))
        Log.d("huy-test-avatar",salon.avatar.toString())
        data[position].address?.let { addressMap ->
            val streetName = addressMap["streetName"]
            val streetNumber = addressMap["streetNumber"]
            val ward = addressMap["ward"]
            val district = addressMap["district"]
            val city = addressMap["city"]
            val addr = "$streetNumber, $streetName, phường $ward, $district, $city"
            holder.salonItemUserBinding.addressSalonItemTV.setText(addr)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}