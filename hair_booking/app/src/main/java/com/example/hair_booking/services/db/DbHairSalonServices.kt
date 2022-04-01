package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.FirebaseFirestore

class DbHairSalonServices(private var dbInstance: FirebaseFirestore?):DatabaseAbstract() {
    override suspend fun find(query:Any): Any {
        TODO("Not yet implemented")
    }

    override suspend fun save(data: Any): Any {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableLiveData<ArrayList<Salon>> {
        Log.d("huy-test-service","Function find all data from Firebase invoke")
        val data:ArrayList<Salon> = arrayListOf()
        val result = MutableLiveData<ArrayList<Salon>>()
        dbInstance!!.collection(Constant.collection.hairSalons)
            .get()
            .addOnSuccessListener { result ->
                for(document in result){
                    Log.d("huy-test-service","Function find all data from Firebase success")
                    val dataInDoc = document.data
                    val salon = Salon(document.id,
                        dataInDoc["name"] as String,
                        dataInDoc["salonAvatar"] as String,
                        dataInDoc["description"] as String,
                        dataInDoc["rate"] as Long,
                        dataInDoc["openHour"] as String,
                        dataInDoc["closeHour"] as String,
                        dataInDoc["address"] as HashMap<String, String>,
                        dataInDoc["appointments"] as ArrayList<HashMap<String, *>>,
                        dataInDoc["stylists"] as ArrayList<HashMap<String, *>>
                    )
                    data.add(salon)
                }
                for (item in data) {
                    Log.d("huy-test-service",item.name.toString())
                }

            }
            .addOnCompleteListener {
                Log.d("huy-test-service","Function find all data from Firebase complete")
            }
            .addOnCanceledListener {
                Log.d("huy-test-service","Cancel")
            }
            .addOnFailureListener{ er ->
                Log.d("huy-test-service",er.toString())
            }
        result.value = data
        return result
    }

    override suspend fun findById(data: Any): Any {
        TODO("Not yet implemented")
    }

    override suspend fun updateOne(id: String, updateDoc: Any): Any {
        TODO("Not yet implemented")
    }

    override suspend fun delete(data: Any): Any {
        TODO("Not yet implemented")
    }
}