package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Appointment
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    suspend fun getAppointmentTimeRanges(bookingDate: String, bookingShiftId: String): ArrayList<Pair<BigDecimal, Int>> {

        var appointmentTimeRanges: ArrayList<Pair<BigDecimal, Int>> = ArrayList()
        var addValueToAppointmentTimeRanges: Deferred<Unit>? = null

        if (dbInstance != null) {
            val shiftDocRef = dbInstance!!
            .collection("shifts")
                .document(bookingShiftId)

            val result = dbInstance!!.collection("appointments")
                .whereEqualTo("bookingShift", shiftDocRef)
                .whereEqualTo("bookingDate", bookingDate)
                .get()
                .await()

            Log.d("xk", "got time range from database")
            addValueToAppointmentTimeRanges = GlobalScope.async {
                for(document in result.documents) {
                    val serviceId: String = ((document.data?.get("service") as HashMap<Any, Any>)["id"] as DocumentReference).id

                    var serviceDuration: Int = 0
                    async {
                        serviceDuration = dbServices.getServiceServices()!!.getServiceDuration(serviceId)
                        Log.d("xk", "got service duration for calc time range")
                    }.await()
                    Log.d("xk", "start to calc time range")
                    val timeRange: Pair<BigDecimal, Int> = Pair((document["bookingTime"] as String).toBigDecimal(), serviceDuration)

                    appointmentTimeRanges.add(timeRange)
                }
            }

        }
        addValueToAppointmentTimeRanges?.await()
        Log.d("xk", "finish calc time range and return")
        return appointmentTimeRanges
    }
}