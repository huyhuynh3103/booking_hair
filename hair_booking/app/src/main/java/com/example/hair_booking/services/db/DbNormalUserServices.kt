package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Discount
import com.example.hair_booking.model.NormalUser
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception

class DbNormalUserServices(private var dbInstance: FirebaseFirestore?) : DatabaseAbstract<Any>() {

    override suspend fun find(data: Any): Any {
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

    suspend fun getUserDiscountPoint(userId: String): Long {

        var discountPoint: Long = 0

        if (dbInstance != null) {
            val result = dbInstance!!.collection(Constant.collection.normalUsers)
                .whereEqualTo("id", userId)
                .get()
                .await()

            for(document in result.documents) {
                discountPoint = document.data?.get("discountPoint") as Long
            }

        }
        return discountPoint
    }

    suspend fun getUserById(userId: String): NormalUser? {
        var user: NormalUser? = null

        if (dbInstance != null) {
            val result = dbInstance!!.collection(Constant.collection.normalUsers)
                .document(userId)
                .get()
                .await()

            user = NormalUser(
                result.id,
                result.data?.get("fullName") as String,
                result.data?.get("phoneNumber") as String,
                result.data?.get("gender") as String,
                result.data?.get("discountPoint") as Long
            )
        }

        return user
    override suspend fun updateOne(id: String, updateDoc: Any): Any {
        TODO("Not yet implemented")
    }

    override fun delete(data: Any): Any {
        TODO("Not yet implemented")
    }
}