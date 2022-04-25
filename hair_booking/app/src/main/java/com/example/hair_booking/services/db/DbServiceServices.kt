package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.model.Service
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.github.mikephil.charting.data.BarEntry
import java.lang.Exception

class DbServiceServices(private var dbInstance: FirebaseFirestore?) {

    fun getServiceListForBooking(): MutableLiveData<ArrayList<Service>> {
        var result = MutableLiveData<ArrayList<Service>>()
        var serviceList: ArrayList<Service> = ArrayList()
        if(dbInstance != null) {
            dbInstance!!.collection(Constant.collection.services)
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
                        
                        // Insert to list if service is not deleted
                        if(document.data?.get("deleted") == null)
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
                result.data?.get("description") as String,
                result.data?.get("duration") as Long,
            )
        }

        return service
    }

    fun getServiceListForAdmin(serviceList: MutableLiveData<ArrayList<Service>>) {
        var tmpServiceList: ArrayList<Service> = ArrayList()

        if(dbInstance != null) {
            dbInstance!!.collection(Constant.collection.services)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val documents = snapshot.documents
                        tmpServiceList.clear()
                        for (document in documents) {
                            // Mapping firestore object to kotlin model
                            val service = Service(
                                document.id,
                                document.data?.get("title") as String,
                                document.data?.get("price") as Long,
                                document.data?.get("description") as String,
                                document.data?.get("duration") as Long
                            )
                            // Insert to list if service is not deleted
                            if(document.data?.get("deleted") == null)
                                tmpServiceList.add(service)
                        }

                        // Call function to return appointment list after mapping complete
                        serviceList.postValue(tmpServiceList)
                    }
                }
        }
    }

    suspend fun saveService(service: HashMap<String, *>): Boolean {
        var ack: Boolean = false
        // Save service to database
        try {
            if(dbInstance != null && service != null) {
                dbInstance!!.collection(Constant.collection.services)
                    .add(service)
                    .await()

                ack = true
            }
        }
        catch (e: Exception) {
            Log.e("DbAppointmentServices", "Error adding document", e)
        }

        return ack
    }

    suspend fun updateService(serviceId: String, service: HashMap<String, *>): Boolean {
        var ack: Boolean = false
        // Save service to database
        try {
            if(dbInstance != null && service != null) {
                dbInstance!!.collection(Constant.collection.services)
                    .document(serviceId)
                    .set(service, SetOptions.merge())
                    .await()

                ack = true
            }
        }
        catch (e: Exception) {
            Log.e("DbAppointmentServices", "Error updating document", e)
        }

        return ack
    }

    suspend fun deleteService(serviceId: String): Boolean {
        var ack: Boolean = false
        // Add "deleted: true" to database
        try {
            if(dbInstance != null) {
                dbInstance!!.collection(Constant.collection.services)
                    .document(serviceId)
                    .set(hashMapOf(
                        "deleted" to true
                    ), SetOptions.merge())
                    .await()

                ack = true
            }
        }
        catch (e: Exception) {
            Log.e("DbAppointmentServices", "Error deleting document", e)
        }

        return ack
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