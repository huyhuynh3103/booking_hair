package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DbSalonServices(private var dbInstance: FirebaseFirestore?) {

    fun getSalonListForBooking(): MutableLiveData<ArrayList<Salon>> {
        var result = MutableLiveData<ArrayList<Salon>>()
        var salonList: ArrayList<Salon> = ArrayList()
        if(dbInstance != null) {
            dbInstance!!.collection("hairSalons")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Mapping firestore object to kotlin model
                        var salon: Salon = Salon(
                            document.id,
                            document.data["name"] as String,
                            document.data["salonAvatar"] as String,
                            document.data["description"] as String,
                            document.data["address"] as HashMap<String, String>
                        )
                        // Insert to list
                        salonList.add(salon)
                    }

                    // Call function to return salon list after mapping complete
                    result.value = salonList
                }
                .addOnFailureListener { exception ->
                    Log.d("xk", "get failed with ", exception)
                }
        }
        return result
    }

    fun getSalonDetail(id: String): MutableLiveData<Salon> {
        var result = MutableLiveData<Salon>()

        if(dbInstance != null) {
            dbInstance!!.collection("hairSalons")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == id) {
                            // Mapping firestore object to kotlin model
                            var salon: Salon = Salon(
                                document.id,
                                document.data["name"] as String,
                                document.data["salonAvatar"] as String,
                                document.data["description"] as String,
                                document.data["rate"] as Long,
                                document.data["openHour"] as String,
                                document.data["closeHour"] as String,
                                document.data["address"] as HashMap<String, String>
                            )

                            result.value = salon
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("SalonServicesLog", "Get salon detail fail with ", exception)
                }
        }

        return result
    }
}