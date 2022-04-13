package com.example.hair_booking.services.booking

import java.math.BigDecimal
import java.math.MathContext

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

        fun setPrecision(num: Float, precision: Int): Float {
            // Use BigDecimal to round Float
            return num.toBigDecimal().round(MathContext(precision)).toFloat()
        }

        fun timeToDisplayToTimeInHour(time: Float): Float {
            // Time in float format: 21.20 => 21h20m

            val timeInString = time.toString()
            // Split hour part and minute part
            val splitter = timeInString.split('.')
            val hourPartInFloat = splitter[0].toFloat() // get hour part and convert back to float
            val minutePartInFloat = splitter[1].toFloat() // get minute part and convert back to float

            // Convert minute to hour, ex: 30 min => 0.5 hour
            var minuteInHour: Float = (minutePartInFloat / 60)
            minuteInHour = setPrecision(minuteInHour, 2)

            // Plus minute in hour with hour part
            val timeInHour = hourPartInFloat + minuteInHour

            return timeInHour
        }

        fun timeInHourToTimeForDisplay(time: Float): Float {
            // Time in hour: 21.5 => time to display: 21h30m

            val timeInString = time.toString()
            // Split hour part and minute part
            val splitter = timeInString.split('.')
            val hourPartInFloat = splitter[0].toFloat() // get hour part and convert back to float
            val minutePartInFloat =
                splitter[1].toFloat() // get minute part and convert back to float

            // Convert hour to minute, ex: 0.5 hour => 30min
            var minuteToDisplay: Float = minutePartInFloat * 60
            minuteToDisplay = setPrecision(minuteToDisplay, 2)

            // Concatenate hour part with new minute part
            return "$hourPartInFloat.$minuteToDisplay".toFloat()
        }

        fun add(timeToDisplayA: Float, timeToDisplayB: Float): Float {
            var result = timeToDisplayToTimeInHour(timeToDisplayA) + timeToDisplayToTimeInHour(timeToDisplayB)

            return timeInHourToTimeForDisplay(result)
        }

        fun minus(timeToDisplayA: Float, timeToDisplayB: Float): Float {
            var result = timeToDisplayToTimeInHour(timeToDisplayA) - timeToDisplayToTimeInHour(timeToDisplayB)

            return timeInHourToTimeForDisplay(result)
        }
    }
}