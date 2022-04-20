package com.example.hair_booking.services.db

import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.NormalUser
import com.example.hair_booking.model.Salon
import com.example.hair_booking.model.Stylist
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import java.text.FieldPosition

class DbAccountServices(private var dbInstance: FirebaseFirestore?) {

    fun foo() {
        Log.d("xk", "adaldqlweql")
    }

     fun getAccountDetail(id: String): MutableLiveData<Account> {
        var detail = MutableLiveData<Account>()

        if(dbInstance != null) {
            dbInstance!!.collection("accounts")
                .get()
                .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.id == id) {
                        // Mapping firestore object to kotlin model
                        var account: Account = Account(
                            document.id,
                            document.data!!["email"] as String,
                            document.data!!["role"] as String,
                            document.data!!["banned"] as Boolean
                        )
                        detail.value = account
                    }
                }
            }.addOnFailureListener { exception ->
                    Log.d("xk", "get failed with ", exception)
                }
        }

        return detail
    }

    suspend fun getAccountStatus(id: String): Boolean {
        var status: Boolean = false
        if (dbInstance != null) {
            val result = dbInstance!!.collection("accounts")
                .document(id)
                .get()
                .await()
            status = result.data?.get("banned") as Boolean
        }
        return status
    }

    suspend fun accountManagerDetail(id: String): Account {
        var detail = Account("")

        if(dbInstance != null) {
            val result = dbInstance!!.collection("accounts")
                .get()
                .await()
            for (document in result.documents) {
                if (document.id == id) {
                    // Mapping firestore object to kotlin model
                    var account: Account = Account(
                        document.id,
                        document.data!!["email"] as String,
                        document.data!!["role"] as String,
                        document.data!!["banned"] as Boolean,
                        document.data!!["hairSalon"] as DocumentReference
                    )
                    detail = account
                }
            }
        }
        return detail
    }

    suspend fun accountDetail(id: String): Account {
        var detail = Account("")

        if(dbInstance != null) {
            val result = dbInstance!!.collection("accounts")
                .get()
                .await()
            for (document in result.documents) {
                if (document.id == id) {
                    // Mapping firestore object to kotlin model
                    var account: Account = Account(
                        document.id,
                        document.data!!["email"] as String,
                        document.data!!["role"] as String,
                        document.data!!["banned"] as Boolean
                    )
                    detail = account
                }
            }
        }
        return detail
    }

    suspend fun getAccountListForManagement(): MutableLiveData<ArrayList<Account>> {
        var list = MutableLiveData<ArrayList<Account>>()
        var accountList: ArrayList<Account> = ArrayList()
        if(dbInstance != null) {
            val result = dbInstance!!.collection("accounts")
                .whereEqualTo("role", "manager")
                .get()
                .await()

            GlobalScope.async {
                    for (document in result.documents) {
                        // Mapping firestore object to kotlin model
                        var account: Account = Account(
                            document.id,
                            document.data!!["email"] as String,
                            document.data!!["role"] as String,
                            document.data!!["banned"] as Boolean,
                            document.data!!["hairSalon"] as DocumentReference
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

    suspend fun updateLockUserAccount(status: String,userId: String) {
        val dbNormalUserServices = dbServices.getNormalUserServices()!!

        // Get account id
        val accountId: String = dbNormalUserServices.getNormalUserAccountId(userId)

        var banned : Boolean = true
        if (status.equals("Đã khóa"))
            banned = false

        val accountRef = dbInstance!!.collection(Constant.collection.accounts).document(accountId)

        accountRef
            .update("banned", banned)
            .addOnSuccessListener {
                Log.d("DbAccountServices", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.d("DbAccountServices", "Error updating document", e)
            }

    }

    suspend fun updateLockManagerAccount(managerId: String) {

        var banned : Boolean = getAccountStatus(managerId)
        banned = !banned

        val accountRef = dbInstance!!.collection(Constant.collection.accounts).document(managerId)

        accountRef
            .update("banned", banned)
            .addOnSuccessListener {
                Log.d("DbAccountServices", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.d("DbAccountServices", "Error updating document", e)
            }

    }

    suspend fun updateManager(selectedID: String, managerId: String) {

        val dbSalonServices = dbServices.getSalonServices()!!

        val workplaceRef: DocumentReference? = dbSalonServices.getWorkplace(selectedID)

        if (dbInstance != null) {
            val accountRef = dbInstance!!.collection(Constant.collection.accounts).document(managerId)
            accountRef
                .update(
                    "hairSalon", workplaceRef)
                .addOnSuccessListener {
                Log.d("DbAccountServices", "DocumentSnapshot successfully updated!")
            }
                .addOnFailureListener { e ->
                    Log.d("DbAccountServices", "Error updating document", e)
                }
        }
    }

}