package com.example.hair_booking.services.db

import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Account
import com.example.hair_booking.model.Salon
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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



    override suspend fun save(data: Any?) : DocumentReference? {
        var task:DocumentReference? = null
        try {
            if(data != null)
                task  =  dbInstance!!.collection(Constant.collection.accounts).add(data).await()
        }catch (e: Exception){
            Log.d("huy-exception","Save new account failed")
            throw e
        }
        return task
    }


//    override suspend fun findAll(): MutableLiveData<ArrayList<Salon>> {
//        val data:ArrayList<Salon> = ArrayList()
//        val res = MutableLiveData<ArrayList<Salon>>()
//        if(dbInstance!=null){
//            dbInstance!!.collection(Constant.collection.hairSalons)
//                .get()
//                .addOnSuccessListener { result ->
//                    for(document in result){
//                        val dataInDoc = document.data
//                        val salon = Salon(document.id,
//                            dataInDoc["name"] as String,
//                            dataInDoc["salonAvatar"] as String,
//                            dataInDoc["description"] as String,
//                            dataInDoc["rate"] as Long,
//                            dataInDoc["openHour"] as String,
//                            dataInDoc["closeHour"] as String,
//                            dataInDoc["address"] as HashMap<String, String>,
//                            dataInDoc["appointments"] as ArrayList<HashMap<String, *>>,
//                            dataInDoc["stylists"] as ArrayList<HashMap<String, *>>
//                        )
//                        data.add(salon)
//                    }
//                    for (item in data) {
//                        Log.d("huy-test-service",item.avatar.toString())
//                    }
//                    res.value = data
//                }
//                .addOnFailureListener{ er ->
//                    Log.d("huy-test-service",er.toString())
//                }
//        }
//
//        return res
//    }

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

    override suspend fun findAll(): Any? {
        TODO("Not yet implemented")
    }
}