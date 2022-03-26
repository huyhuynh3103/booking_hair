package com.example.hair_booking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hair_booking.firebase.Database
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    // Get firestore instance
    private val firebaseInstance: FirebaseFirestore = Database.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbServices.getNormalUserServices()?.foo()
    }

}