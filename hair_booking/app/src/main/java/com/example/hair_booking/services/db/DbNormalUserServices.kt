package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.NormalUser
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class DbNormalUserServices(private var dbInstance: FirebaseFirestore?) : DatabaseAbstract() {

    override fun find(data: Any): Any {
        TODO("Not yet implemented")
    }

    override suspend fun save(data: Any): DocumentReference {
        Log.d("huy-save-normal-user","start")
        var docRef:DocumentReference?
        try {
            docRef = dbInstance!!.collection(Constant.collection.normalUsers).add(data).await()
        }catch (e: Exception){
            Log.d("huy-exception","Save new User failed",e)
            throw e
        }
        return docRef
    }


    override fun findAll(): Any {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): MutableLiveData<NormalUser> {
        var result = MutableLiveData<NormalUser>()

        if (dbInstance != null) {
            dbInstance!!.collection("normalUsers")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == id) {
                            // Mapping firestore object to kotlin model
                            var normalUser = NormalUser(
                                document.id,
                                document.data["fullName"] as String,
                                document.data["phoneNumber"] as String,
                                document.data["gender"] as String,
                                document.data["discountPoint"] as Long
                            )

                            result.value = normalUser
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("NormalUserServicesLog", "Get normal user detail fail with ", exception)
                }
        }

        return result
    }

    override suspend fun updateOne(id: String, updateDoc: Any): Any {
        TODO("Not yet implemented")
    }

    override fun delete(data: Any): Any {
        TODO("Not yet implemented")
    }
}