package com.example.hair_booking.services.db

import com.example.hair_booking.firebase.Database
import com.google.firebase.firestore.FirebaseFirestore

object dbServices {
    private var dbInstance: FirebaseFirestore? = null
    private var normalUserServices: DbNormalUserServices? = null
    private var accountServices: DbAccountServices? = null
    private var salonServices: DbSalonServices? = null
    private var stylistServices: DbStylistServices? = null
    private var serviceServices: DbServiceServices? = null

    init {
        dbInstance = Database.getInstance()
        normalUserServices = DbNormalUserServices(dbInstance)
        salonServices = DbSalonServices(dbInstance)
        stylistServices = DbStylistServices(dbInstance)
        serviceServices = DbServiceServices(dbInstance)
        accountServices = DbAccountServices(dbInstance)
    }

    fun getNormalUserServices(): DbNormalUserServices? {
        return normalUserServices
    }

    fun getAccountServices(): DbAccountServices? {
        return accountServices
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

    // GETTER
    val hairSalonServices = DbHairSalonServices(dbInstance)
}
