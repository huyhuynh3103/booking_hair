package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.FirebaseFirestore

class DbHairSalonServices(private var dbInstance: FirebaseFirestore?):DatabaseAbstract() {
    override fun find(data: Any): Any {
        TODO("Not yet implemented")
    }

    override fun save(data: Any): Any {
        TODO("Not yet implemented")
    }


    override fun findAll(): MutableLiveData<ArrayList<Salon>> {
        val data:ArrayList<Salon> = arrayListOf()
        val res = MutableLiveData<ArrayList<Salon>>()
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
                    Log.d("huy-test-service",item.avatar.toString())
                }
                res.value = data
            }
            .addOnCompleteListener {
            }
            .addOnCanceledListener {
                Log.d("huy-test-service","Cancel")
            }
            .addOnFailureListener{ er ->
                Log.d("huy-test-service",er.toString())
            }

        return res
    }

    override fun findById(data: Any): Any {
        TODO("Not yet implemented")
    }

    override fun updateOne(id: String, updateDoc: Any): Any {
        TODO("Not yet implemented")
    }

    override fun delete(data: Any): Any {
        TODO("Not yet implemented")
    }

}