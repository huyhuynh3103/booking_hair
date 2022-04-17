package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Stylist
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DbStylistServices(private var dbInstance: FirebaseFirestore?) : DatabaseAbstract() {

    override fun find(data: Any?): Any? {
        TODO("Not yet implemented")
    }

    override fun save(data: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): ArrayList<Stylist> {
        var stylistList: ArrayList<Stylist> = ArrayList()

        try {
            val docSnap = dbInstance!!.collection("stylists")
                .whereEqualTo("deleted", false)
                .get()
                .await()

            for (document in docSnap.documents) {
                // Mapping firestore object to kotlin model
                var stylist = Stylist(
                    document.id,
                    document.data?.get("fullName") as String,
                    document.data?.get("avatar") as String,
                    document.data?.get("description") as String,
                    document.data?.get("workPlace") as DocumentReference,
                    document.data?.get("shifts") as HashMap<String, HashMap<*, *>>,
                    document.data?.get("deleted") as Boolean,
                )

                // Insert to list
                stylistList.add(stylist)
            }
        } catch (exception: Exception) {
            Log.d("StylistServicesLog", "Get stylist detail fail with ", exception)
        }

        // Call function to return salon list after mapping complete
        return stylistList
    }

    override suspend fun findById(id: Any?): Stylist? {
        var stylist: Stylist? = null

        try {
            val docSnap = dbInstance!!.collection("stylists")
                .document(id!!.toString())
                .get()
                .await()

            stylist = Stylist(
                docSnap.id,
                docSnap.data?.get("fullName") as String,
                docSnap.data?.get("avatar") as String,
                docSnap.data?.get("description") as String,
                docSnap.data?.get("workPlace") as DocumentReference,
                docSnap.data?.get("shifts") as HashMap<String, HashMap<*, *>>,
                docSnap.data?.get("deleted") as Boolean,
            )
        } catch (exception: Exception) {
            Log.d("StylistServicesLog", "Get stylist detail fail with ", exception)
        }

        // Call function to return salon list after mapping complete
        return stylist
    }

    override suspend fun updateOne(id: Any?, updateDoc: Any?): Any? {
        TODO("Not yet implemented")
    }

    suspend fun updateOne(id: Any?, updateDoc: Stylist?) {
        if (dbInstance != null) {
            val docSnap = dbInstance!!.collection("stylists")
                .document(id!!.toString())
                .update(
                    "fullName", updateDoc?.fullName,
                    "description", updateDoc?.description,
                    "workPlace", updateDoc?.workPlace
                )
                .await()
        }
    }

    override suspend fun delete(id: Any?) {
        if (dbInstance != null) {
            val docSnap = dbInstance!!.collection("stylists")
                .document(id!!.toString())
                .update(
                    "deleted", true
                )
                .await()
        }
    }

    override suspend fun add(data: Any?) {
        TODO("Not yet implemented")
    }

    suspend fun add(data: Stylist) {
        val docToSave = hashMapOf(
            "fullName" to data.fullName,
            "avatar" to data.avatar,
            "description" to data.description,
            "workPlace" to data.workPlace,
            "shifts" to data.shift,
            "deleted" to data.deleted
        )

        if (dbInstance != null && docToSave != null) {
            val docSnap = dbInstance!!.collection("stylists")
                .add(docToSave)
                .await()
        }
    }

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

    fun getStylistListForBooking(): MutableLiveData<ArrayList<Stylist>> {
        var result = MutableLiveData<ArrayList<Stylist>>()
        var stylistList: ArrayList<Stylist> = ArrayList()
        if (dbInstance != null) {
            dbInstance!!.collection("stylists")
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
                                document.data["workPlace"] as DocumentReference,
                                document.data["workPlace"] as HashMap<String, HashMap<*, *>>,
                                document.data["deleted"] as Boolean
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