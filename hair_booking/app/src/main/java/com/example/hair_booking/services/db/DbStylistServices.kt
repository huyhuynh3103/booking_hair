package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Service
import com.example.hair_booking.model.Stylist
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class DbStylistServices(private var dbInstance: FirebaseFirestore?): DatabaseAbstract() {

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
                                document.data["shifts"] as HashMap<*, *>,
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
                result.data?.get("shifts") as HashMap<*, *>,
                result.data?.get("workPlace") as DocumentReference
            )
        }

        return stylist
    }

    override suspend fun find(query: Any): Any {
        TODO("Not yet implemented")
    }

    override fun save(data: Any): Any {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): ArrayList<Stylist> {
        var stylistList: ArrayList<Stylist> = ArrayList()
        try {
            val result = dbInstance!!.collection("stylists")
                .get()
                .await()


            for(document in result.documents) {
                val stylist: Stylist = Stylist(
                    document.id as String,
                    document.data?.get("fullName") as String,
                    document.data?.get("avatar") as String,
                    document.data?.get("description") as String,
                    document.data?.get("shifts") as HashMap<*, *>,
                    document.data?.get("workPlace") as DocumentReference,
                )

                stylistList.add(stylist)
            }
        }
        catch (exception: Exception) {
            Log.e("DbStylistServices: ", exception.toString())
        }
        return stylistList
    }

    override suspend fun findById(data: Any): Any {
        TODO("Not yet implemented")
    }

    override fun updateOne(id: String, updateDoc: Any): Any {
        TODO("Not yet implemented")
    }

    override fun delete(data: Any): Any {
        TODO("Not yet implemented")
    }

}