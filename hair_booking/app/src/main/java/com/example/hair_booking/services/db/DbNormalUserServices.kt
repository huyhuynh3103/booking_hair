package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class DbNormalUserServices(private var dbInstance: FirebaseFirestore?) {

    fun foo() {
        Log.d("xk", "adaldqlweql")
    }

    fun getNormalUserDetail(id: String): MutableLiveData<NormalUser> {
        var detail = MutableLiveData<NormalUser>()

        if(dbInstance != null) {
            val result = dbInstance!!.collection("normalUsers")
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
                }
        }

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
                    val accountId: String =
                        (document.data?.get("accountId") as DocumentReference).id
                        var account: Account =
                            dbServices.getAccountServices()!!.getAccountDetail(accountId)
                        Log.d("tdht", account.username!!)

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


                    // Insert to list

                }

            }

        }
        addValueToUserListManagement?.await()
        // Call function to return salon list after mapping complete
        list.value = userList
        return list
    }
}