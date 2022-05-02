package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

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

    suspend fun FindAll(): ArrayList<Salon> {
        var salonList: ArrayList<Salon> = ArrayList()

        try {
            val docSnap = dbInstance!!.collection("hairSalons")
                .whereEqualTo("deleted", false)
                .get()
                .await()

            for (document in docSnap.documents) {
                var rate: Double = if (document.data?.get("rate") is Long) {
                    Double.fromBits(document.data?.get("rate") as Long)
                } else {
                    document.data?.get("rate") as Double
                }

                // Mapping firestore object to kotlin model
                var salon = Salon(
                    document.id,
                    document.data?.get("name") as String,
                    document.data?.get("salonAvatar") as String,
                    document.data?.get("description") as String,
                    rate,
                    document.data?.get("openHour") as String,
                    document.data?.get("closeHour") as String,
                    document.data?.get("address") as HashMap<String, String>
                )

                // Insert to list
                salonList.add(salon)
            }
        } catch (exception: Exception) {
            Log.d("StylistServicesLog", "Get stylist detail fail with ", exception)
        }

        // Call function to return salon list after mapping complete
        return salonList
    }

    override suspend fun findById(id: Any?): MutableLiveData<Salon> {
        var result = MutableLiveData<Salon>()

        if (dbInstance != null) {
            dbInstance!!.collection("hairSalons")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == id) {
                            var rate:Double
                            if(document.data["rate"] is Long){
                                rate = Double.fromBits(document.data["rate"] as Long)
                            }else{
                                rate = document.data["rate"] as Double
                            }
                            // Mapping firestore object to kotlin model
                            var salon: Salon = Salon(
                                document.id,
                                document.data["name"] as String,
                                document.data["salonAvatar"] as String,
                                document.data["description"] as String,
                                rate,
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
    suspend fun setSalonRatingById(id:String,rating:Float){
        if (dbInstance != null) {
            val docQuery = dbInstance!!.collection(Constant.collection.hairSalons)
                .document(id)
                .get()
                .await()
            val data = docQuery.data
            val numRates = data?.get("numRates") as Long?
            var rate:Double
            if(data?.get("rate") is Long){
                rate = Double.fromBits(data?.get("rate") as Long)
            }else{
                rate = data?.get("rate") as Double
            }
            val newRating = ((rate!!*numRates!!)+rating)/(numRates+1)
            val newNumRates = numRates + 1
            val updatedField = hashMapOf<String,Any>(
                "rate" to newRating,
                "numRates" to newNumRates
            )
            dbInstance!!.collection(Constant.collection.hairSalons)
                .document(id)
                .update(updatedField)
                .await()
        }
    }

    override suspend fun delete(id: Any?) {
        if (dbInstance != null) {
            val docSnap = dbInstance!!.collection("hairSalons")
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

    suspend fun add(data: Salon) {
        val docToSave = hashMapOf(
            "name" to data.name,
            "salonAvatar" to data.avatar,
            "description" to data.description,
            "numRates" to 0,
            "rate" to data.rate,
            "openHour" to data.openHour,
            "closeHour" to data.closeHour,
            "address" to data.address,
            "phoneNumber" to data.phoneNumber,
            "appointments" to ArrayList<DocumentReference>(),
            "stylists" to ArrayList<DocumentReference>(),
            "deleted" to data.deleted
        )

        if (dbInstance != null && docToSave != null) {
            val docSnap = dbInstance!!.collection("hairSalons")
                .add(docToSave)
                .await()
        }
    }

    override suspend fun updateOne(id: Any?, updateDoc: Any?): Any? {
        TODO("Not yet implemented")
    }

    suspend fun updateOne(id: Any?, updateDoc: Salon?) {
        if (dbInstance != null) {
            val docSnap = dbInstance!!.collection("hairSalons")
                .document(id!!.toString())
                .update(
                    "name", updateDoc?.name,
                    "description", updateDoc?.description,
                    "openHour", updateDoc?.openHour,
                    "closeHour", updateDoc?.closeHour,
                    "address", updateDoc?.address,
                    "phoneNumber", updateDoc?.phoneNumber
                )
                .await()
        }
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

    suspend fun getSalonListForStatistics(): ArrayList<Salon> {
        var salonList: ArrayList<Salon> = ArrayList()
        if (dbInstance != null) {
            val result = dbInstance!!.collection("hairSalons")
                .get()
                .await()
            for (document in result.documents) {
                // Mapping firestore object to kotlin model
                var salon: Salon = Salon(
                    document.id,
                    document.data?.get("name") as String,
                    document.data!!["address"] as HashMap<String, String>
                )
                // Insert to list
                salonList.add(salon)
            }

        }
        return salonList
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
                            var rate: Double
                            if (document.data["rate"] is Long) {
                                rate = Double.fromBits(document.data["rate"] as Long)
                            }
                            else {
                                rate = document.data["rate"] as Double
                            }

                            var salon: Salon = Salon(
                                document.id,
                                document.data["name"] as String,
                                document.data["salonAvatar"] as String,
                                document.data["description"] as String,
                                rate,
                                document.data["openHour"] as String,
                                document.data["closeHour"] as String,
                                document.data["address"] as HashMap<String, String>,
                                document.data["phoneNumber"] as String,
                                document.data["deleted"] as Boolean,
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
            var rate:Double
            if(result.data?.get("rate") is Long){
                rate = Double.fromBits(result.data?.get("rate") as Long)
            }else{
                rate = result.data?.get("rate") as Double
            }
            salon = Salon(
                result.id,
                result.data?.get("name") as String,
                result.data?.get("salonAvatar") as String,
                result.data?.get("description") as String,
                rate,
                result.data?.get("openHour") as String,
                result.data?.get("closeHour") as String,
                result.data?.get("address") as HashMap<String, String>,
                result.data?.get("appointments") as ArrayList<DocumentReference>,
                result.data?.get("stylists") as ArrayList<HashMap<String, *>>,
                result.data?.get("phoneNumber") as String,
                result.data?.get("deleted") as Boolean,
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