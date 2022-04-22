package com.example.hair_booking.services.local

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hair_booking.model.Discount
import com.example.hair_booking.services.booking.DateServices
import com.example.hair_booking.services.booking.TimeServices
import com.example.hair_booking.services.db.DbNormalUserServices
import com.example.hair_booking.services.db.DbServiceServices
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DiscountServices {
    companion object {
        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun getUnusedDiscounts(userId: String, chosenDate: String, serviceId: String): ArrayList<Discount> {
            val discountServices = dbServices.getDiscountServices()!!
            val appointmentServices = dbServices.getAppointmentServices()!!
            var unusedDiscount: ArrayList<Discount> = ArrayList(discountServices.getDiscountsByServiceId(serviceId))
            var usedDiscountIds: ArrayList<String?> = ArrayList()
            GlobalScope.async {
                usedDiscountIds = appointmentServices.getAppliedDiscountIds(userId, serviceId)
            }.await()


            unusedDiscount.removeIf { discount ->
                usedDiscountIds.contains(discount.id) || isExpired(chosenDate, discount.dateExpired!!)
            }
            return unusedDiscount
        }

        private fun isExpired(chosenDate: String, dateExpired: String): Boolean {
            // Check if discount is expired
            if(DateServices.isAfter(chosenDate, dateExpired))
                return true
            return false
        }

        fun canApplied(userId: String,
                               userCurrentPoint: Long,
                               chosenDate: String,
                               dateApplied: String,
                               discountRequiredPoint: Long): Boolean {
            // Check if discount is expired
            if(DateServices.isAfter(chosenDate, dateApplied) && userCurrentPoint >= discountRequiredPoint)
                return true
            return false
        }
    }
}