package com.example.hair_booking.services.booking

import android.util.Log
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

class BigDecimalTimeServices {

    companion object {
        fun toBigDecimal(num: Float): BigDecimal {
            return num.toBigDecimal().setScale(2)
        }

        fun isLargerThan(timeA: BigDecimal, timeB: BigDecimal): Boolean {
            return timeA > timeB
        }

        fun IntMinuteToBigDecimal(minute: Int): BigDecimal {
            return (minute.toFloat()/100).toBigDecimal()
        }

        fun IntMinuteToBigDecimalHour(minute: Int): BigDecimal {
            return (minute.toFloat()/60).toBigDecimal().setScale(2)
        }

        fun plusMinute(hour: BigDecimal, minute: BigDecimal): BigDecimal {
            var result = hour.round(MathContext(2))
            val maxMinuteInHourFormat = BigDecimal(0.6).round(MathContext(1))
            if(minute > maxMinuteInHourFormat) {
                result += BigDecimal(1.0) + (minute - maxMinuteInHourFormat)
            }

            return result
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

            val timeInString = time.toString()
            // Split hour part and minute part
            val splitter = timeInString.split('.')
            val hourPartInFloat = splitter[0].toFloat() // get hour part and convert back to float
            Log.d("xk", "hour part in float: $hourPartInFloat")
            val minutePartInFloat = splitter[1].toFloat() // get minute part and convert back to float
            Log.d("xk", "minute part in float: $minutePartInFloat")
            // Convert minute to hour, ex: 30 min => 0.5 hour
            var minuteInHour: Float = (minutePartInFloat / 60)
            Log.d("xk", "minute part in hour: $minuteInHour")
            minuteInHour = round(minuteInHour, 4)
            Log.d("xk", "minute part in hour after precision: $minuteInHour")
            // Plus minute in hour with hour part
            val timeInHour = hourPartInFloat + minuteInHour

            Log.d("xk", "timeInHour: $timeInHour")
            return timeInHour
        }

        fun timeInHourToTimeForDisplay(time: Float): Float {
//            // Time in hour: 21.5 => time to display: 21h30m
////            Log.d("xk", "time in hour: $time")
//            val timeInString = setPrecision(time, 2).toString()
////            Log.d("xk", "time in string: $timeInString")
//            // Split hour part and minute part
//            val splitter = timeInString.split('.')
//            val hourPartInFloat = splitter[0] // get hour part and convert back to float
//            var minutePartInFloat = setPrecision(splitter[1].toFloat(), 2) / 10 // get minute part and convert back to float
////            Log.d("xk", "minute precision before /10: ${setPrecision(splitter[1].toFloat(), 2) / 10}")
////            Log.d("xk", "minute precision after /10: ${setPrecision(splitter[1].toFloat() / 10, 2)}")
//            minutePartInFloat = setPrecision(minutePartInFloat, 2)
//            // Convert hour to minute, ex: 0.5 hour => 30min
//            var minuteToDisplay: Int = (minutePartInFloat * 60).toInt()
////            Log.d("xk", (minutePartInFloat * 60).toString())
////            Log.d("xk", (minutePartInFloat * 60).toInt().toString())
////            Log.d("xk", setPrecision(minutePartInFloat * 60, 2).toString())
////
////            Log.d("xk", "time in display: $hourPartInFloat.$minuteToDisplay")
//            // Concatenate hour part with new minute part
//            return "$hourPartInFloat.$minuteToDisplay".toFloat()
            val timeInInt = (time * 60).toInt()
            return "${timeInInt/60}.${timeInInt%60}".toFloat()
        }

        fun addTimeToDisplay(timeToDisplayA: Float, timeToDisplayB: Float): Float {
            var result = timeToDisplayToTimeInHour(timeToDisplayA) + timeToDisplayToTimeInHour(timeToDisplayB)
            return timeInHourToTimeForDisplay(result)
        }

        fun minusTimeToDisplay(timeToDisplayA: Float, timeToDisplayB: Float): Float {
            Log.d("xk", "minus time to display: timeA $timeToDisplayA, timeB $timeToDisplayB")
            var result = timeToDisplayToTimeInHour(timeToDisplayA) - timeToDisplayToTimeInHour(timeToDisplayB)

            return timeInHourToTimeForDisplay(result)
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
    }
}