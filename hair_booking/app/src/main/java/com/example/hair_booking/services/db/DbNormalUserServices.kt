package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class DbNormalUserServices(private var dbInstance: FirebaseFirestore?) {

    fun foo() {
        Log.d("xk", "adaldqlweql")
    }

    fun getNormalUserDetail(id: String): MutableLiveData<NormalUser> {
        var detail = MutableLiveData<NormalUser>()
        if(dbInstance != null) {
            dbInstance!!.collection("normalUsers")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == id) {
                            val accountId: String =
                                (document.data?.get("accountId") as DocumentReference).id

                            //var account: Account = dbServices.getAccountServices()!!.getAccountDetail(accountId)
                            // Mapping firestore object to kotlin model
                            var normalUser: NormalUser = NormalUser(
                                document.id,
                                document.data!!["fullName"] as String,
                                document.data!!["phoneNumber"] as String,
                                document.data!!["gender"] as String,
                                document.data!!["discountPoint"] as Long,
                                document.data!!["accountId"] as DocumentReference
                            )

                            detail.value = normalUser
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.d("xk", "get failed with ", exception)
                }
        }
        return detail
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
}