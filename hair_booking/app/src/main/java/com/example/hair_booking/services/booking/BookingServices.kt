package com.example.hair_booking.services.booking

import android.util.Log
import com.example.hair_booking.model.Shift
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.roundToInt

class BookingServices {

    companion object {
        fun generateTimeBasedOnShift(shift: Shift, serviceDuration: Int): ArrayList<String> {
            val averageTimeBetweenService: BigDecimal = BigDecimal(0.3).round(MathContext(2))

            var minTime = shift.startHour?.toBigDecimal()

            // User's chosen time + chosen service duration/60 <= the shift's end hour
            // Note: chosen service duration is stored in minutes
            // => maxTime = the shift's end hour - (chosen service duration/60) = max available time for user to choose with specific service
            var maxTime = (shift.endHour?.toBigDecimal()!! - serviceDuration.toBigDecimal()/ BigDecimal(60))


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
            bookingShiftId: String)
        : ArrayList<Int> {
            var disabledTimePositions: ArrayList<Int> = ArrayList()

            var chosenServiceDurationInBigDecimal = (chosenServiceDuration.toFloat().toBigDecimal()/BigDecimal(60.0))

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

                    val totalTimeRange = timeCanBePickedInBigDecimal + chosenServiceDurationInBigDecimal

                    appointmentTimeRanges.forEach {
                        var totalAppointmentTimeRange = it.first + (it.second.toFloat().toBigDecimal()/BigDecimal(60.0))
                        val intPartOfTotalAppointmentTimeRange = totalAppointmentTimeRange.toString().split('.')[0].toBigDecimal() // get int part
                        val floatPartOfTotalAppointmentTimeRange = (totalAppointmentTimeRange - intPartOfTotalAppointmentTimeRange)// get float part

                        if(floatPartOfTotalAppointmentTimeRange == BigDecimal(0.6).round(MathContext(2))) {
                            // Case current = xx.6 => convert to (xx + 1).0
                            // Ex: 18.6 => convert to 19.0
                            totalAppointmentTimeRange = intPartOfTotalAppointmentTimeRange.plus(BigDecimal(1)).round(MathContext(2))
                        }

                        if(timeCanBePickedInBigDecimal >= it.first
                                    && totalTimeRange <= totalAppointmentTimeRange) {
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

