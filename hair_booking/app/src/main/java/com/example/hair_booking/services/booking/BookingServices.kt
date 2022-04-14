package com.example.hair_booking.services.booking

import android.util.Log
import com.example.hair_booking.model.Shift
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.collections.ArrayList

class BookingServices {
    companion object {

        // The average time between item in list of time display for user to pick
        // is based on the average time of services
        // In this case: the average time of services = 30 min => format in float will be: 0.5
        private val averageTimeBetweenService: Float = BigDecimalTimeServices.round(0.5F, 4)
        fun generateTimeBasedOnShift(shift: Shift, serviceDuration: Int, isToday: Boolean): ArrayList<String> {
            // Set the minimum time in list of time display for user to pick to the start hour of the shift
            val minTime = shift.startHour!!.toFloat()

            // Convert serviceDuration from minute to hour
            // Ex: 45 min (int type) => 0.75 hour
            var serviceDurationInHour: Float = BigDecimalTimeServices.minuteToHour(serviceDuration)
            var maxTime = BigDecimalTimeServices.minusTimeToDisplay(shift.endHour!!.toFloat(), BigDecimalTimeServices.timeInHourToTimeForDisplay(serviceDurationInHour))


            // Init available time with minTime
            var availableTime: ArrayList<String> = arrayListOf(BigDecimalTimeServices.setPrecision(minTime, 2).toString())

            var currentInHour = minTime!!
            while(currentInHour < maxTime) {
                currentInHour = BigDecimalTimeServices.addTimeInHour(currentInHour, averageTimeBetweenService)

                // Ex: current = 15.2 (float) => current = "15.20" (string)
                var tmp: Float = BigDecimalTimeServices.timeInHourToTimeForDisplay(currentInHour)
                val timeToDisplay: String = BigDecimalTimeServices.setPrecision(tmp, 2).toString()
                availableTime.add(timeToDisplay)
            }
            return availableTime
        }

        suspend fun getDisabledTimePositions(
            chosenServiceDuration: Int,
            availableTime: ArrayList<String>,
            bookingDate: String,
            bookingShiftId: String,
            isToday: Boolean)
        : ArrayList<Int> {
            var disabledTimePositions: ArrayList<Int> = ArrayList()

            // Convert duration from minute to hour
            // Ex: 45 min (int type) => 0.75 hour
            var chosenServiceDurationInHour = BigDecimalTimeServices.minuteToHour(chosenServiceDuration)

            val disableTime = GlobalScope.async {
                var appointmentTimeRanges: ArrayList<Pair<Float, Int>> = ArrayList()
                async {
                    Log.d("xk", "bookserv")
                    // The first element in pair is bookingTime, the second one is service duration
                    appointmentTimeRanges = dbServices.getAppointmentServices()!!.getAppointmentTimeRanges(bookingDate, bookingShiftId)
                }.await()

                Log.d("xk", "finish get app time range, and in bookserv")
                // Compare each time range can be picked to all time range of appointment booked in database
                for(timeCanBePicked in availableTime) {
                    // Convert timeCanBePicked from display format to hour, ex: 21.30 (21h30) => 21.5 hour
                    var timeCanBePickedInHour: Float = BigDecimalTimeServices.timeToDisplayToTimeInHour(timeCanBePicked.toFloat())
                    // Estimated end time = time user picked + service picked duration
                    var estimatedEndTime = BigDecimalTimeServices.addTimeInHour(timeCanBePickedInHour, chosenServiceDurationInHour)


                    for(timeRange in appointmentTimeRanges) {
                        // Time database returned is in display format => convert to hour
                        val userPickedTimeInHour = BigDecimalTimeServices.timeToDisplayToTimeInHour(timeRange.first)
                        val userPickedServiceDurationInHour = BigDecimalTimeServices.minuteToHour(timeRange.second)

                        // Appointment end time = time user picked + service picked duration
                        var appointmentEndTime = BigDecimalTimeServices.addTimeInHour(userPickedTimeInHour, userPickedServiceDurationInHour)


                        // Compare time range can be picked to time range of appointment booked in database
                        // If they conflict => the time can be picked became disabled
                        if(timeCanBePickedInHour >= userPickedTimeInHour
                            && estimatedEndTime <= appointmentEndTime) {
                            disabledTimePositions.add(availableTime.indexOf(timeCanBePicked))
                        }
                        else if(isToday) {
                            // Disable all the time before the current time (user cannot pick the time that already passed)
                            val currentTimeToDisplay: Float = BigDecimalTimeServices.getCurrentTimeInFloatFormat()
                            val currentTimeInHour: Float = BigDecimalTimeServices.timeToDisplayToTimeInHour(currentTimeToDisplay)

                            if(timeCanBePickedInHour < currentTimeInHour)
                                disabledTimePositions.add(availableTime.indexOf(timeCanBePicked))
                        }

                    }
                }

            }
            disableTime.await()
            return disabledTimePositions
        }
    }
}

