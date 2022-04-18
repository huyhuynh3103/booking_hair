package com.example.hair_booking.services.booking

import java.text.SimpleDateFormat
import java.util.*

class DateServices {
    companion object {
        private val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        fun isAfter(firstDateInString: String, secondDateInString: String): Boolean {
            val firstDate: Date = sdf.parse(firstDateInString)
            val secondDate: Date = sdf.parse(secondDateInString)

            return firstDate.after(secondDate)
        }

        fun isBefore(firstDateInString: String, secondDateInString: String): Boolean {
            val firstDate: Date = sdf.parse(firstDateInString)
            val secondDate: Date = sdf.parse(secondDateInString)

            return firstDate.before(secondDate)
        }

        fun currentDateInString(): String {
            val currentDate: Date = Date()
            return sdf.format(currentDate)
        }
    }
}