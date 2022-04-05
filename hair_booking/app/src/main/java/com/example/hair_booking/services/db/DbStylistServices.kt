package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Stylist
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class DbStylistServices(private var dbInstance: FirebaseFirestore?) {

    fun getStylistListForBooking(chosenSalonId: String): MutableLiveData<ArrayList<Stylist>> {
        var result = MutableLiveData<ArrayList<Stylist>>()
        var stylistList: ArrayList<Stylist> = ArrayList()
        if (dbInstance != null) {
            val hairSalonDocRef = dbInstance!!
                .collection("hairSalons")
                .document(chosenSalonId)

            dbInstance!!.collection("stylists")
                .whereEqualTo("workPlace", hairSalonDocRef)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Mapping firestore object to kotlin model
                        var stylist: Stylist = Stylist(
                            document.id,
                            document.data["fullName"] as String,
                            document.data["avatar"] as String,
                            document.data["description"] as String
                        )
                        // Insert to list
                        stylistList.add(stylist)
                    }

                    // Call function to return salon list after mapping complete
                    result.value = stylistList
                }
                .addOnFailureListener { exception ->
                    Log.d("xk", "get failed with ", exception)
                }
        }
        return result
    }

    fun getStylistDetail(id: String): MutableLiveData<Stylist> {
        var result = MutableLiveData<Stylist>()

        if (dbInstance != null) {
            dbInstance!!.collection("stylists")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == id) {
                            // Mapping firestore object to kotlin model
                            var stylist: Stylist = Stylist(
                                document.id,
                                document.data["fullName"] as String,
                                document.data["avatar"] as String,
                                document.data["description"] as String,
                                document.data["workPlace"] as DocumentReference
                            )

                            result.value = stylist
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("StylistServicesLog", "Get stylist detail fail with ", exception)
                }
        }

        return result
    }

}