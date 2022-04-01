package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore

class DbAppointmentServices(private var dbInstance: FirebaseFirestore?) {

    fun getAppointmentListForManager(): MutableLiveData<ArrayList<Appointment>> {
        var result = MutableLiveData<ArrayList<Appointment>>()
        var appointmentList: ArrayList<Appointment> = ArrayList()

        if(dbInstance != null) {
            dbInstance!!.collection("appointments")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Mapping firestore object to kotlin model
                        var appointment: Appointment = Appointment(
                            document.id,
                            document.data["subId"] as String,
                            document.data["userFullName"] as String,
                            document.data["stylist"] as HashMap<String, *>,
                            document.data["bookingDate"] as String,
                            document.data["bookingTime"] as String,
                            document.data["createdAt"] as String,
                            document.data["status"] as String
                        )
                        // Insert to list
                        appointmentList.add(appointment)
                    }

                    // Call function to return appointment list after mapping complete
                    result.value = appointmentList
                }
                .addOnFailureListener { exception ->
                    Log.d("xk", "get failed with ", exception)
                }
        }
        return result
    }
}