package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DbSalonServices(private var dbInstance: FirebaseFirestore?) : DatabaseAbstract<Any?>() {

    override suspend fun find(data: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun save(data: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): MutableLiveData<ArrayList<Salon>> {
        var result = MutableLiveData<ArrayList<Salon>>()
        var salonList: ArrayList<Salon> = ArrayList()
        if (dbInstance != null) {
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

    override suspend fun findById(id: Any?): MutableLiveData<Salon> {
        var result = MutableLiveData<Salon>()

        if (dbInstance != null) {
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

    override suspend fun updateOne(id: Any?, updateDoc: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun add(data: Any?): Any? {
        TODO("Not yet implemented")
    }

    fun getSalonListForBooking(): MutableLiveData<ArrayList<Salon>> {
        var result = MutableLiveData<ArrayList<Salon>>()
        var salonList: ArrayList<Salon> = ArrayList()
        if (dbInstance != null) {
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

    suspend fun getWorkplace(id: String?): DocumentReference? {
        var workplaceRef: DocumentReference? = null

        if (dbInstance != null) {
            val docSnap = dbInstance!!.collection("hairSalons")
                .document(id!!)
                .get()

            workplaceRef = docSnap.await().reference
        }

        return workplaceRef
    }

    fun getSalonDetail(id: String): MutableLiveData<Salon> {
        var result = MutableLiveData<Salon>()

        if (dbInstance != null) {
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


    suspend fun getSalonById(salonId: String): Salon? {
        var salon: Salon? = null

        if (dbInstance != null) {
            val result = dbInstance!!.collection(Constant.collection.hairSalons)
                .document(salonId)
                .get()
                .await()

            salon = Salon(
                result.id,
                result.data?.get("name") as String,
                result.data?.get("salonAvatar") as String,
                result.data?.get("description") as String,
                result.data?.get("rate") as Long,
                result.data?.get("openHour") as String,
                result.data?.get("closeHour") as String,
                result.data?.get("address") as HashMap<String, String>,
                result.data?.get("appointments") as ArrayList<DocumentReference>,
                result.data?.get("stylists") as ArrayList<HashMap<String, *>>,
                result.data?.get("phoneNumber") as String
            )
        }

        return salon
    }

    fun updateHairSalonWhenBooking(salonId: String, appointment: DocumentReference) {

        val hairSalonRef = dbInstance!!.collection(Constant.collection.hairSalons).document(salonId)

        hairSalonRef
            .update(
                "appointments", FieldValue.arrayUnion(appointment)
            )
            .addOnSuccessListener {
                Log.d("DbNormalUserServices", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.d("DbNormalUserServices", "Error updating document", e)
            }

    }
}