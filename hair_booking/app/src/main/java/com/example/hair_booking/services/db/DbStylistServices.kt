package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Service
import com.example.hair_booking.model.Stylist
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class DbStylistServices(private var dbInstance: FirebaseFirestore?) : DatabaseAbstract<Any?>() {
    override suspend fun find(data: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun save(data: Any?): Any? {
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
                    document.data?.get("shifts") as HashMap<String, HashMap<String, *>>,
                    document.data?.get("workPlace") as DocumentReference,
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

    suspend fun findAll(salonID: DocumentReference?): ArrayList<Stylist> {
        var stylistList: ArrayList<Stylist> = ArrayList()

        try {
            val docSnap = dbInstance!!.collection("stylists")
                .whereEqualTo("workPlace", salonID)
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
                    document.data?.get("shifts") as HashMap<String, HashMap<String, *>>,
                    document.data?.get("workPlace") as DocumentReference,
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
                docSnap.data?.get("shifts") as HashMap<String, HashMap<String, *>>,
                docSnap.data?.get("workPlace") as DocumentReference,
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
                    "workPlace", updateDoc?.workPlace,
                    "shifts", updateDoc?.shifts
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

    override suspend fun add(data: Any?): Any? {
        TODO("Not yet implemented")
    }

    suspend fun add(data: Stylist) {
        val docToSave = hashMapOf(
            "fullName" to data.fullName,
            "avatar" to data.avatar,
            "description" to data.description,
            "workPlace" to data.workPlace,
            "shifts" to data.shifts,
            "deleted" to data.deleted
        )

        if (dbInstance != null && docToSave != null) {
            val docSnap = dbInstance!!.collection("stylists")
                .add(docToSave)
                .await()
        }
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
                                document.data["shifts"] as HashMap<String, HashMap<String, *>>,
                                document.data["workPlace"] as DocumentReference,
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

    suspend fun getStylistById(stylistId: String): Stylist? {
        var stylist: Stylist? = null

        if (dbInstance != null) {
            val result = dbInstance!!.collection(Constant.collection.stylists)
                .document(stylistId)
                .get()
                .await()
            stylist = Stylist(
                result.id,
                result.data?.get("fullName") as String,
                result.data?.get("avatar") as String,
                result.data?.get("description") as String,
                result.data?.get("shifts") as HashMap<String, HashMap<String, *>>,
                result.data?.get("workPlace") as DocumentReference,
                result.data?.get("deleted") as Boolean,
            )
        }

        return stylist
    }
}