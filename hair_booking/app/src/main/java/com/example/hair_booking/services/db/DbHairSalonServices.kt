package com.example.hair_booking.services.db

import android.util.Log
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

    override suspend fun findAll(): ArrayList<Salon> {
        val data:ArrayList<Salon> = arrayListOf()
        dbInstance!!.collection(Constant.collection.hairSalons)
            .get()
            .addOnSuccessListener { result ->
                for(document in result){
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
                    Log.d("huy-test",item.name.toString())
                }
            }
            .addOnCompleteListener {
                Log.d("huy-test","Complete")
            }
            .addOnCanceledListener {
                Log.d("huy-test","Cancel")
            }
            .addOnFailureListener{ er ->
                Log.d("huy-test",er.toString())
            }
        return data
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