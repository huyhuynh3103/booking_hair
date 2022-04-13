package com.example.hair_booking.services.booking

import android.util.Log
import com.example.hair_booking.model.Shift
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.math.BigDecimal
import java.math.MathContext
import java.util.*
import kotlin.collections.ArrayList

class BookingServices {
    companion object {

        // The average time between item in list of time display for user to pick
        // is based on the average time of services
        // In this case: the average time of services = 30 min => format in float will be: 0.3
        private val averageTimeBetweenService: BigDecimal = BigDecimal(0.3).round(MathContext(2))
        fun generateTimeBasedOnShift(shift: Shift, serviceDuration: Int, isToday: Boolean): ArrayList<String> {


            // Set the minimum time in list of time display for user to pick to the start hour of the shift
            val minTime = shift.startHour!!.toBigDecimal()

            // User's chosen time + chosen service duration/60 <= the shift's end hour
            // Note: chosen service duration is stored in minutes
            // => maxTime = the shift's end hour - (chosen service duration/60) = max available time for user to choose with specific service
//            var maxTime = (shift.endHour?.toBigDecimal()!! - serviceDuration.toBigDecimal()/ BigDecimal(60))

            // Convert serviceDuration from minute to hour in Big Decimal
            // Ex: 45 min (int type) => 0.75 hour (
            var serviceDurationInHour = BigDecimalTimeServices.IntMinuteToBigDecimalHour(serviceDuration)
            var maxTime = (shift.endHour?.toBigDecimal()!! - serviceDurationInHour)



            // Round maxTime to nearest number which fractional part is multiple of 0.30
            // Average time between services (considered num of stylist) = 30p => 0.30
            // ex: case fractional part < 0.30: maxTime = 15.22 => 15.00
            // case fractional part < 0.30: maxTime = 15.13 => 15.00
            // case fractional part > 0.30: maxTime = 15.40 => 15.30
            val intPartOfMaxTime = maxTime.toString().split('.')[0].toBigDecimal() // get int part
            if(maxTime - intPartOfMaxTime < averageTimeBetweenService) {
                // case fractional part < 0.30 => round down 0.00
                maxTime = intPartOfMaxTime
            }
            else {
                // case fractional part > 0.30 => round up to 0.30
                maxTime = (intPartOfMaxTime + averageTimeBetweenService)
            }


            // Init available time with minTime
            var availableTime: ArrayList<String> = arrayListOf(minTime.toString())

            var current = minTime!!
            while(current < maxTime) {
                current = (current + averageTimeBetweenService)

                val intPartOfCurrent = current.toString().split('.')[0].toBigDecimal() // get int part
                val floatPartOfCurrent = (current - intPartOfCurrent)// get float part

                var currentInString: String = ""
                if(floatPartOfCurrent == BigDecimal(0.6).round(MathContext(2))) {
                    // Case current = xx.6 => convert to (xx + 1).0
                    // Ex: 18.6 => convert to 19.0
                    current = intPartOfCurrent.plus(BigDecimal(1)).round(MathContext(2))
                    currentInString = "$current.00"
                }
                else {
                    currentInString = current.toString()
                }


                // Convert to string to display, ex: 15.30 => 15:30
    //                val timeToDisplay: String = current.toString().replace('.', ':')
                availableTime.add(currentInString)
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

            var chosenServiceDurationInBigDecimal = BigDecimalTimeServices.IntMinuteToBigDecimalHour(chosenServiceDuration)

            val disableTime = GlobalScope.async {
                var appointmentTimeRanges: ArrayList<Pair<BigDecimal, Int>> = ArrayList()
                async {
                    Log.d("xk", "bookserv")
                    // The first element in pair is bookingTime, the second one is service duration
                    appointmentTimeRanges = dbServices.getAppointmentServices()!!.getAppointmentTimeRanges(bookingDate, bookingShiftId)
                }.await()

                Log.d("xk", "finish get app time range, and in bookserv")
                availableTime.forEach { timeCanBePicked ->
                    var timeCanBePickedInBigDecimal = timeCanBePicked.toBigDecimal()

                    var userChosenEndTime = BigDecimalTimeServices.plusMinute(timeCanBePickedInBigDecimal, chosenServiceDurationInBigDecimal)

                    for(item in appointmentTimeRanges) {
//                        var totalAppointmentTimeRange = item.first + (item.second.toFloat().toBigDecimal()/BigDecimal(60.0))
                        var appointmentEndTime = BigDecimalTimeServices.plusMinute(item.first, BigDecimalTimeServices.IntMinuteToBigDecimalHour(item.second))
//                        val intPartOfUserChosenEndTime = appointmentEndTime.toString().split('.')[0].toBigDecimal() // get int part
//                        val floatPartOfUserChosenEndTime = (appointmentEndTime - intPartOfUserChosenEndTime)// get float part
//
//                        if(floatPartOfUserChosenEndTime == BigDecimal(0.6).round(MathContext(2))) {
//                            // Case current = xx.6 => convert to (xx + 1).0
//                            // Ex: 18.6 => convert to 19.0
//                            appointmentEndTime = intPartOfUserChosenEndTime.plus(BigDecimal(1)).round(MathContext(2))
//                        }


                        if(timeCanBePickedInBigDecimal >= item.first
                            && userChosenEndTime <= appointmentEndTime) {
                            disabledTimePositions.add(availableTime.indexOf(timeCanBePicked))
                        }
                        else if(isToday) {
                            val now = Calendar.getInstance()
                            val currentHourIn24Format: Float = now.get(Calendar.HOUR_OF_DAY).toFloat()// return the hour in 24 hrs format (ranging from 0-23)
                            val currentMinute: Int = now.get(Calendar.MINUTE) // return minute
                            val currentTime = BigDecimalTimeServices.toBigDecimal(currentHourIn24Format) + BigDecimalTimeServices.IntMinuteToBigDecimal(currentMinute)

                            if(timeCanBePickedInBigDecimal < currentTime)
                                disabledTimePositions.add(availableTime.indexOf(timeCanBePicked))
                        }

                    }
//                    appointmentTimeRanges.forEach {
//
//                    }
                }
            }

            disableTime.await()
            return disabledTimePositions
        }
    }
}

