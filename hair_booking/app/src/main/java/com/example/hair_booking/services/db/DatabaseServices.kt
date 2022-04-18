package com.example.hair_booking.services.db

import com.example.hair_booking.firebase.Database
import com.google.firebase.firestore.FirebaseFirestore

object dbServices {
    private var dbInstance: FirebaseFirestore? = null
    private var normalUserServices: DbNormalUserServices? = null
    private var salonServices: DbSalonServices? = null
    private var stylistServices: DbStylistServices? = null
    private var serviceServices: DbServiceServices? = null
    private var appointmentServices: DbAppointmentServices? = null
    private var shiftServices: DbShiftServices? = null
    private var discountServices: DbDiscountServices? = null

    init {
        dbInstance = Database.getInstance()
        normalUserServices = DbNormalUserServices(dbInstance)
        salonServices = DbSalonServices(dbInstance)
        stylistServices = DbStylistServices(dbInstance)
        serviceServices = DbServiceServices(dbInstance)
        appointmentServices = DbAppointmentServices(dbInstance)
        shiftServices = DbShiftServices(dbInstance)
        discountServices = DbDiscountServices(dbInstance)
    }

    fun getNormalUserServices(): DbNormalUserServices? {
        return normalUserServices
    }

    fun getSalonServices(): DbSalonServices? {
        return salonServices
    }

    fun getStylistServices(): DbStylistServices? {
        return stylistServices
    }

    fun getServiceServices(): DbServiceServices? {
        return serviceServices
    }

    fun getAppointmentServices(): DbAppointmentServices? {
        return appointmentServices
    }

    fun getShiftServices(): DbShiftServices? {
        return shiftServices
    }

    fun getDiscountServices(): DbDiscountServices? {
        return discountServices
    }

    // GETTER
    val hairSalonServices = DbHairSalonServices(dbInstance)
}
