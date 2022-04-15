package com.example.hair_booking.services.local

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hair_booking.model.Discount
import com.example.hair_booking.services.db.DbServiceServices
import com.example.hair_booking.services.db.dbServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class DiscountServices {
    companion object {
        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun getUnusedDiscounts(userId: String): ArrayList<Discount> {
            val discountServices = dbServices.getDiscountServices()!!
            val appointmentServices = dbServices.getAppointmentServices()!!
            var unusedDiscount: ArrayList<Discount> = ArrayList(discountServices.findAll())
            var usedDiscountIds: ArrayList<String> = ArrayList()
            GlobalScope.async {
                usedDiscountIds = appointmentServices.getAppliedDiscountIds(userId)
            }.await()
            
            unusedDiscount.removeIf { discount ->
                usedDiscountIds.contains(discount.id)
            }

            return unusedDiscount
        }
    }
}