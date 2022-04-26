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
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.*

class DbNormalUserServices(private var dbInstance: FirebaseFirestore?) : DatabaseAbstract<Any>() {

    override suspend fun find(data: Any): Any {
        TODO("Not yet implemented")
    }

    override suspend fun save(data: Any?): DocumentReference? {
        Log.d("huy-save-normal-user","start")
        var docRef:DocumentReference? = null
        if(data != null)
            docRef = dbInstance!!.collection(Constant.collection.normalUsers).add(data).await()
        return docRef
    }


    override suspend fun findAll(): Any {
        TODO("Not yet implemented")
    }

    suspend fun getNormalUserAccountId(id: String): String {
        var accountId: String = ""
        if (dbInstance != null) {
            val result = dbInstance!!.collection("normalUsers")
                .get()
                .await()
            for (document in result.documents) {
                if (document.id == id) {
                    accountId = (document.data?.get("accountId") as DocumentReference).id
                }
            }
        }
        return accountId
    }

    suspend fun getNormalUserAccountDetail(id: String): Account {
        var detail = Account("")
        var addValueToUserAccountDetail: Deferred<Unit>? = null
        if(dbInstance != null) {
            val accountId = getNormalUserAccountId(id)

            val result = dbInstance!!.collection("normalUsers")
                .get()
                .await()
            addValueToUserAccountDetail = GlobalScope.async {
                    for (document in result.documents) {
                        if (document.id == id) {
                            var account = Account("")
                            async {
                                account = dbServices.getAccountServices()!!.accountDetail(accountId)
                                Log.d("tdht123", account.email.toString())
                            }.await()
                            detail = account
                        }
                    }

                }
        }
        addValueToUserAccountDetail?.await()
        Log.d("tdht123", detail.toString())
        return detail
    }

    suspend fun getUserListForManagement(): MutableLiveData<ArrayList<NormalUser>> {
        var list = MutableLiveData<ArrayList<NormalUser>>()
        var userList: ArrayList<NormalUser> = ArrayList()
        var addValueToUserListManagement: Deferred<Unit>? = null
        if(dbInstance != null) {
            val result = dbInstance!!.collection("normalUsers")
                .get()
                .await()
            addValueToUserListManagement = GlobalScope.async {
                for (document in result.documents) {

                    //Log.d("tdht", account.username!!)
                    // Mapping firestore object to kotlin

                        var normalUser: NormalUser = NormalUser(
                            document.id,
                            document.data!!["fullName"] as String,
                            document.data!!["phoneNumber"] as String,
                            document.data!!["gender"] as String,
                            document.data!!["discountPoint"] as Long,
                            document.data!!["accountId"] as DocumentReference
                        )
                        userList.add(normalUser)

                }

            }

        }
        addValueToUserListManagement?.await()
        // Call function to return salon list after mapping complete
        list.value = userList
        return list
    }

    suspend fun updateNormalUser(fullname: String, phone: String, gender: String, id: String) {

        val normalUserRef = dbInstance!!.collection(Constant.collection.normalUsers).document(id)

        normalUserRef
            .update("fullName", fullname, "phoneNumber", phone, "gender", gender)
            .addOnSuccessListener {
                Log.d("DbNormalUserServices", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.d("DbNormalUserServices", "Error updating document", e)
            }

    }

    suspend fun getUserDiscountPoint(userId: String): Long {

        var discountPoint: Long = 0

        if (dbInstance != null) {
            val document = dbInstance!!.collection(Constant.collection.normalUsers)
                .document(userId)
                .get()
                .await()

            discountPoint = document.data?.get("discountPoint") as Long

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
                result.data?.get("discountPoint") as Long,
                result.data?.get("accountId") as DocumentReference
            )
        }

        return user
    }

    fun updateNormalUserWhenBooking(userId: String, appointment: DocumentReference) {

        val normalUserRef = dbInstance!!.collection(Constant.collection.normalUsers).document(userId)

        normalUserRef
            .update(
                "appointments", FieldValue.arrayUnion(appointment),
                "discountPoint", FieldValue.increment(500)
            )
            .addOnSuccessListener {
                Log.d("DbNormalUserServices", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.d("DbNormalUserServices", "Error updating document", e)
            }

    }

    suspend fun getUserByAccountId(accountId: String): NormalUser? {
        var user: NormalUser? = null

        if (dbInstance != null) {
            val accountRef = dbInstance!!.collection(Constant.collection.accounts).document(accountId)
            val result = dbInstance!!.collection(Constant.collection.normalUsers)
                .whereEqualTo("accountId", accountRef)
                .get()
                .await()

            for (document in result.documents) {
                // Mapping firestore object to kotlin
                user = NormalUser(
                    document.id,
                    document.data!!["fullName"] as String,
                    document.data!!["phoneNumber"] as String,
                    document.data!!["gender"] as String,
                    document.data!!["discountPoint"] as Long,
                    document.data!!["wishList"] as ArrayList<DocumentReference>,
                    document.data!!["accountId"] as DocumentReference
                )
            }
        }

        return user
    }

    override suspend fun findById(id: Any?): Any? {
        TODO("Not yet implemented")
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
}