package com.example.hair_booking.ui.normal_user.home

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.model.Salon

class SalonAdapter(private var data:ArrayList<Salon>):RecyclerView.Adapter<SalonAdapter.ViewHolder>() {
    var onItemClick: ((Salon) -> Unit)? = null
    inner class ViewHolder(salonItemView: View):RecyclerView.ViewHolder(salonItemView){
        val salonImg = salonItemView.findViewById<ImageView>(R.id.salonItemImg)
        val salonName = salonItemView.findViewById<TextView>(R.id.nameSalonItemTV)
        val salonAddress = salonItemView.findViewById<TextView>(R.id.addressSalonItemTV)
        val salonRatingBar = salonItemView.findViewById<RatingBar>(R.id.ratingSalonItemBar)

        init {
            salonItemView.setOnClickListener{
                onItemClick?.invoke(data[adapterPosition])
            }
        }

    }
    fun addData(list:List<Salon>){
        data.addAll(list)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalonAdapter.ViewHolder {
        val context = parent.context
        val inflater =LayoutInflater.from(context)
        val salonItemView = inflater.inflate(R.layout.salon_item_user,parent,false)
        return ViewHolder(salonItemView)
    }

    override fun onBindViewHolder(holder: SalonAdapter.ViewHolder, position: Int) {
        holder.salonImg.setImageDrawable(Drawable.createFromPath(data[position].avatar))
        holder.salonName.text = data[position].name
        data[position].address?.let { addressMap ->
            val streetName = addressMap["streetName"]
            val streetNumber = addressMap["streetNumber"]
            val ward = addressMap["ward"]
            val district = addressMap["district"]
            val city = addressMap["city"]
            val addr = "$streetNumber, $streetName, phường $ward, $district, $city"
            holder.salonAddress.setText(addr)
        }
        data[position].rate?.let { rateNumber ->
            holder.salonRatingBar.rating = rateNumber.toFloat()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}