package com.example.hair_booking.services.db

import com.example.hair_booking.firebase.Database
import com.google.firebase.firestore.FirebaseFirestore

object dbServices {
    private var dbInstance: FirebaseFirestore? = null
    private var normalUserServices: DbNormalUserServices? = null

    init {
        dbInstance = Database.getInstance()
        normalUserServices = DbNormalUserServices(dbInstance)
    }

    fun getNormalUserServices(): DbNormalUserServices? {
        return normalUserServices
    }

}
