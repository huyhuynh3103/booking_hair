package com.example.hair_booking.ui.normal_user.booking

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.hair_booking.model.Shift

class ShiftPickerSpinnerAdapter (
    private val context: Activity, private val shiftList: ArrayList<String>
): ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, shiftList){

    init {
        // Set layout for all rows of spinner
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun isEnabled(position: Int): Boolean {
        // Disable the first item from Spinner
        // First item will be the placehoder
        return position != 0
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
        // set the color of first item in the drop down list (placeholder)
        // and items to be disabled to gray
        if(position == 0) {
            view.setTextColor(Color.GRAY)
        }

        return view
    }
}