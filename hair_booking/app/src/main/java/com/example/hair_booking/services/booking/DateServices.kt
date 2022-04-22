package com.example.hair_booking.services.booking

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

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

        @RequiresApi(Build.VERSION_CODES.O)
        fun getTheNLastDaysFromNow(numOfDays: Int): ArrayList<String> {
            var listOfNLastDays: ArrayList<String> = ArrayList()
            for(i in 0 until numOfDays) {
                val tmp: LocalDate = LocalDate.now(ZoneId.systemDefault()).minusDays(i.toLong())
                listOfNLastDays.add(tmp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            }
            return listOfNLastDays
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getTheNLastMonthsFromNow(numOfMonths: Int): ArrayList<Pair<Int, Int>> {
            var listOfNLastMonths: ArrayList<Pair<Int, Int>> = ArrayList()
            for(i in 0 until numOfMonths) {
                val tmp: LocalDate = LocalDate.now(ZoneId.systemDefault()).minusMonths(i.toLong())
                listOfNLastMonths.add(Pair(tmp.monthValue, tmp.year))
            }
            return listOfNLastMonths
        }
    }
}