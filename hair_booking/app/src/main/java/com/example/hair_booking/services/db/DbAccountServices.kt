package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.suspendCoroutine

class DbAccountServices(private var dbInstance: FirebaseFirestore?):DatabaseAbstract<HashMap<String,String>>() {
    override suspend fun find(query: HashMap<String,String>): ArrayList<Account> {

        var arrayUsers = ArrayList<Account>()

        val querySnapshot = dbInstance!!.collection(Constant.collection.accounts)
            .get()
            .await()
        for(document in querySnapshot){
            val dataInDoc = document.data
            var passQueryCondition = true
            for(key in query.keys){
                if(query[key]!=dataInDoc[key]){
                    passQueryCondition = false
                    break
                }
            }
            if(passQueryCondition){
                arrayUsers.add(Account(document.id,
                    dataInDoc["email"] as String,
                    dataInDoc["role"] as String,
                    dataInDoc["banned"] as Boolean
                ))
            }
        }
        return arrayUsers
    }



    override suspend fun save(data: Any) : DocumentReference {
        Log.d("huy-save-account","start")
        val task:DocumentReference?
        try {
            task  =  dbInstance!!.collection(Constant.collection.accounts).add(data).await()
        }catch (e: Exception){
            Log.d("huy-exception","Save new account failed")
            throw e
        }
        return task
    }


    override fun findAll(): MutableLiveData<ArrayList<Salon>> {
        val data:ArrayList<Salon> = ArrayList()
        val res = MutableLiveData<ArrayList<Salon>>()
        if(dbInstance!=null){
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
                .addOnFailureListener{ er ->
                    Log.d("huy-test-service",er.toString())
                }
        }

        return res
    }

    override fun findById(id: String): Any {
        TODO("Not yet implemented")
    }

    override suspend fun updateOne(id: String, updateDoc: Any): Any {
        TODO("Not yet implemented")
    }

    override fun delete(data: Any): Any {
        TODO("Not yet implemented")
    }

}