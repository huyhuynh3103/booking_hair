package com.example.hair_booking.services.local

import com.example.hair_booking.firebase.Database
import com.google.firebase.firestore.FirebaseFirestore

object LocalServices {
    private var dbInstance: FirebaseFirestore? = null
    private var normalUserServices: NormalUserServices

    init {
        dbInstance = Database.getInstance()
        normalUserServices = NormalUserServices(dbInstance)
    }

    fun getNormalUserServices(): NormalUserServices {
        return normalUserServices
    }

}