package com.example.hair_booking.services.booking

import com.example.hair_booking.model.Shift
import com.example.hair_booking.services.db.dbServices
import kotlin.math.max
import kotlin.math.roundToInt

class BookingServices {

    companion object {
        fun generateTimeBasedOnShift(shift: Shift, serviceDuration: Int): ArrayList<String> {
            val averageTimeBetweenService: Double = 0.30

            var minTime = shift.startHour?.toDouble()

            // User's chosen time + chosen service duration <= the shift's end hour
            // => maxTime = the shift's end hour - chosen service duration = max available time for user to choose with specific service
            var maxTime = shift.endHour?.toDouble()!!.minus(serviceDuration.toDouble())

            // Round maxTime to nearest number which fractional part is multiple of 0.30
            // Average time between services (considered num of stylist) = 30p => 0.30
            // ex: case fractional part < 0.30: maxTime = 15.22 => 15.00
            // case fractional part < 0.30: maxTime = 15.13 => 15.00
            // case fractional part > 0.30: maxTime = 15.40 => 15.30
            val intPartOfMaxTime = maxTime.toString().split('.')[0].toDouble() // get int part of double
            if(maxTime.minus(intPartOfMaxTime) < averageTimeBetweenService) {
                // case fractional part < 0.30 => round down 0.00
                maxTime = intPartOfMaxTime
            }
            else {
                // case fractional part > 0.30 => round up to 0.30
                maxTime = intPartOfMaxTime + averageTimeBetweenService
            }


            // Init available time with minTime
            var availableTime: ArrayList<String> = arrayListOf(minTime.toString())

            var current: Double? = minTime
            while(current!!.compareTo(maxTime) < 0) {
                current += averageTimeBetweenService
                // Convert to string to display, ex: 15.30 => 15:30
//                val timeToDisplay: String = current.toString().replace('.', ':')
                availableTime.add(current.toString())
            }

            return availableTime
        }

        suspend fun getDisabledTimePositions(
            chosenServiceDuration: Int,
            availableTime: ArrayList<String>,
            bookingDate: String,
            bookingShiftId: String): ArrayList<Int> {
            var disabledTimePositions: ArrayList<Int> = ArrayList()


            // The first element in pair is bookingTime, the second one is service duration
            val appointmentTimeRanges: ArrayList<Pair<Double, Int>> =
                dbServices.getAppointmentServices()!!
                    .getAppointmentTimeRanges(bookingDate, bookingShiftId)

            availableTime.forEach { timeCanBePicked ->
                val totalTimeRange: Double = timeCanBePicked.toDouble().plus(chosenServiceDuration)

                appointmentTimeRanges.forEach {
                    val totalAppointmentTimeRange: Double = it.first.plus(it.second)
                    if(!(timeCanBePicked.toDouble() > it.first
                        && totalTimeRange < totalAppointmentTimeRange)) {
                        disabledTimePositions.add(availableTime.indexOf(timeCanBePicked))
                    }
                }
            }


            return disabledTimePositions
        }
    }
}