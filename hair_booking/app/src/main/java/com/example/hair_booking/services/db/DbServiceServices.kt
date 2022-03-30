package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Service
import com.google.firebase.firestore.FirebaseFirestore

class DbServiceServices(private var dbInstance: FirebaseFirestore?) {

    fun getServiceListForBooking(): MutableLiveData<ArrayList<Service>> {
        var result = MutableLiveData<ArrayList<Service>>()
        var serviceList: ArrayList<Service> = ArrayList()
        if(dbInstance != null) {
            dbInstance!!.collection("services")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Mapping firestore object to kotlin model
                        var service: Service = Service(
                            document.id,
                            document.data["title"] as String,
                            document.data["price"] as Long,
                            document.data["description"] as String
                        )
                        // Insert to list
                        serviceList.add(service)
                    }

                    // Call function to return salon list after mapping complete
                    result.value = serviceList
                }
                .addOnFailureListener { exception ->
                    Log.d("xk", "get failed with ", exception)
                }
        }
        return result
    }
}