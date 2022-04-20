package com.example.hair_booking.services.booking

import android.util.Log
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

class TimeServices {

    companion object {
        fun toBigDecimal(num: Float): BigDecimal {
            return num.toBigDecimal().setScale(2)
        }

        fun isLargerThan(timeA: BigDecimal, timeB: BigDecimal): Boolean {
            return timeA > timeB
        }

        fun round(num: Float, numOfRemainingDigits: Int): Float {
            // Use BigDecimal to round Float
            // Ex: numOfRemainingDigit = 4
            // => 17.599999 => 17.60 (4 digits remain)
            return num.toBigDecimal().round(MathContext(numOfRemainingDigits)).toFloat()
        }

        fun setPrecision(num: Float, scale: Int): BigDecimal {
            return num.toBigDecimal().setScale(scale)
        }

        fun minuteToHour(minute: Int): Float {
            return round(minute.toFloat() / 60, 4)
        }

        fun timeToDisplayToTimeInHour(time: Float): Float {
            // Time in float format: 21.20 => 21h20m

            val timeInString = setPrecision(time, 2).toString()
            // Split hour part and minute part
            val splitter = timeInString.split('.')
            val hourPartInFloat = splitter[0].toFloat() // get hour part and convert back to float
            val minutePartInFloat = splitter[1].toFloat() // get minute part and convert back to float
            // Convert minute to hour, ex: 30 min => 0.5 hour
            var minuteInHour: Float = (minutePartInFloat / 60)
            minuteInHour = round(minuteInHour, 4)
            // Plus minute in hour with hour part
            val timeInHour = round(hourPartInFloat + minuteInHour, 4)

            return timeInHour
        }

        fun timeInHourToTimeForDisplay(time: Float): Float {
            val timeInInt = (time * 60).toInt()
            return "${timeInInt/60}.${timeInInt%60}".toFloat()
        }

        fun addTimeToDisplay(timeToDisplayA: Float, timeToDisplayB: Float): Float {
            var result = timeToDisplayToTimeInHour(timeToDisplayA) + timeToDisplayToTimeInHour(timeToDisplayB)
            return timeInHourToTimeForDisplay(result)
        }

        fun minusTimeToDisplay(timeToDisplayA: Float, timeToDisplayB: Float): Float {
            var result = timeToDisplayToTimeInHour(timeToDisplayA) - timeToDisplayToTimeInHour(timeToDisplayB)

            return timeInHourToTimeForDisplay(round(result, 4))
        }

        fun addTimeInHour(timeInHourA: Float, timeInHourB: Float): Float {
            return round(timeInHourA + timeInHourB, 4)
        }

        fun minusTimeInHour(timeInHourA: Float, timeInHourB: Float): Float {
            return round(timeInHourA - timeInHourB, 4)
        }

        fun getCurrentTimeInFloatFormat(): Float {
            val now = Calendar.getInstance()
            val currentHourIn24Format: Int = now.get(Calendar.HOUR_OF_DAY) // return the hour in 24 hrs format (ranging from 0-23)
            val currentMinute: Int = now.get(Calendar.MINUTE) // return minute

            return "$currentHourIn24Format.$currentMinute".toFloat()
        }

        fun checkTimeInHourConflict(timeRangeA: Pair<Float, Float>, timeRangeB: Pair<Float, Float>): Boolean {
            if(timeRangeA.first >= timeRangeB.first && timeRangeA.first <= timeRangeB.second) {
                return true
            }

            if(timeRangeA.second >= timeRangeB.first && timeRangeA.second <= timeRangeB.second) {
                return true
            }
            return false
        }
    }
}