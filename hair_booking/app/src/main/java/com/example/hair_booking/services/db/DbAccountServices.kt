package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class DbAccountServices(private var dbInstance: FirebaseFirestore?) {

    fun foo() {
        Log.d("xk", "adaldqlweql")
    }

    suspend fun getAccountDetail(id: String): Account {
        var detail : Account = Account("")

        if(dbInstance != null) {
            val result = dbInstance!!.collection("accounts")
                .get()
                .await()
            GlobalScope.async {
                for (document in result.documents) {
                    if (document.id == id) {
                        // Mapping firestore object to kotlin model
                        var account: Account = Account(
                            document.id,
                            document.data!!["username"] as String,
                            document.data!!["password"] as String,
                            document.data!!["role"] as String,
                            document.data!!["banned"] as Boolean
                        )
                        detail = account
                    }
                }
            }.await()
        }

        return detail
    }

    suspend fun getAccountListForManagement(): MutableLiveData<ArrayList<Account>> {
        var list = MutableLiveData<ArrayList<Account>>()
        var accountList: ArrayList<Account> = ArrayList()
        if(dbInstance != null) {
            val result = dbInstance!!.collection("accounts")
                .get()
                .await()

            GlobalScope.async {
                    for (document in result.documents) {
                        // Mapping firestore object to kotlin model
                        var account: Account = Account(
                            document.id,
                            document.data!!.get("username") as String,
                            document.data!!["password"] as String,
                            document.data!!["role"] as String,
                            document.data!!["banned"] as Boolean
                        )
                        // Insert to list
                        accountList.add(account)
                    }

                    // Call function to return salon list after mapping complete
                    list.postValue(accountList)
                }.await()
        }
        return list
    }

}