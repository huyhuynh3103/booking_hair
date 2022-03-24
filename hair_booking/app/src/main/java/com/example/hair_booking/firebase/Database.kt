package com.example.hair_booking.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Singleton object of firestore database
object Database {
    private var instance: FirebaseFirestore? = null

    fun getInstance(): FirebaseFirestore {
        if(instance != null)
            return instance as FirebaseFirestore
        
        // If the firestore hasn't init yet => init
        instance = Firebase.firestore
        return instance as FirebaseFirestore
    }
}