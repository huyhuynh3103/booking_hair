package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Service
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

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

    suspend fun getServiceDuration(serviceId: String): Int {
        var serviceDuration: Int = 0
        if (dbInstance != null) {
            val docSnap = dbInstance!!.collection("services")
                .document(serviceId)
                .get()
                .await()

            serviceDuration = (docSnap["duration"] as Long).toInt()
        }
        return serviceDuration
    }

    suspend fun getServiceById(serviceId: String): Service? {
        var service: Service? = null

        if (dbInstance != null) {
            val result = dbInstance!!.collection(Constant.collection.services)
                .document(serviceId)
                .get()
                .await()

            service = Service(
                result.id,
                result.data?.get("title") as String,
                result.data?.get("price") as Long,
                result.data?.get("description") as String
            )
        }

        return service
    }

    suspend fun findAll(): ArrayList<Service> {
        var serviceList: ArrayList<Service> = ArrayList()
        try {
            val result = dbInstance!!.collection(Constant.collection.services)
                .get()
                .await()


            for(document in result.documents) {
                // Mapping firestore object to kotlin model
                val service: Service = Service(
                    document.id,
                    document.data?.get("title") as String,
                    document.data?.get("price") as Long,
                    document.data?.get("description") as String
                )
                // Insert to list
                serviceList.add(service)
            }
        }
        catch (exception: Exception) {
            Log.e("DbServiceServices: ", exception.toString())
        }
        return serviceList
    }

}