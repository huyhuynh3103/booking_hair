package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Discount
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class DbDiscountServices(private var dbInstance: FirebaseFirestore?): DatabaseAbstract<Any?>() {

    suspend fun getDiscountsByServiceId(serviceId: String): ArrayList<Discount> {

        var discountList: ArrayList<Discount> = ArrayList()

        if (dbInstance != null) {
            val serviceDocRef: DocumentReference = dbInstance!!
                .collection(Constant.collection.services)
                .document(serviceId)

            val result = dbInstance!!.collection(Constant.collection.discounts)
                .whereEqualTo("serviceApplied", serviceDocRef)
                .get()
                .await()

            for(document in result.documents) {
                val discount: Discount = Discount(
                    document.id,
                    document.data?.get("title") as String,
                    document.data?.get("requiredPoint") as Long,
                    document.data?.get("description") as String,
                    document.data?.get("dateApplied") as String,
                    document.data?.get("dateExpired") as String,
                    document.data?.get("percent").toString().toDouble(),
                    document.data?.get("serviceApplied") as DocumentReference
                )

                discountList.add(discount)
            }

        }

        return discountList
    }

    suspend fun getDiscountsById(discountId: String): Discount? {

        var discount: Discount? = null

        if (dbInstance != null) {
            val document = dbInstance!!.collection(Constant.collection.discounts)
                .document(discountId)
                .get()
                .await()

            discount = Discount(
                document.id,
                document.data?.get("title") as String,
                document.data?.get("requiredPoint") as Long,
                document.data?.get("description") as String,
                document.data?.get("dateApplied") as String,
                document.data?.get("dateExpired") as String,
                document.data?.get("percent").toString().toDouble(),
                document.data?.get("serviceApplied") as DocumentReference
            )

        }

        return discount
    }

    override suspend fun findAll(): ArrayList<Discount> {

        var discountList: ArrayList<Discount> = ArrayList()

        if (dbInstance != null) {

            val result = dbInstance!!.collection(Constant.collection.discounts)
                .get()
                .await()

            for(document in result.documents) {
                val discount: Discount = Discount(
                    document.id,
                    document.data?.get("title") as String,
                    document.data?.get("requiredPoint") as Long,
                    document.data?.get("description") as String,
                    document.data?.get("dateApplied") as String,
                    document.data?.get("dateExpired") as String,
                    document.data?.get("percent").toString().toDouble(),
                    document.data?.get("serviceApplied") as DocumentReference
                )

                discountList.add(discount)
            }

        }

        return discountList
    }

    suspend fun getDiscountById(id:String):Discount?{
        var result: Discount? = null
        val queryResult = dbInstance!!.collection(Constant.collection.discounts).document(id).get().await()
        val data = queryResult.data
        if(data!=null){
            result = Discount(
                id,
                data["title"] as String,
                data["percent"].toString().toDouble()
            )
        }
        return result
    }

    fun getDiscountListForAdmin(discountList: MutableLiveData<ArrayList<Discount>>) {
        var tmpDiscountList: ArrayList<Discount> = ArrayList()

        if(dbInstance != null) {
            dbInstance!!.collection(Constant.collection.discounts)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val documents = snapshot.documents
                        tmpDiscountList.clear()
                        for (document in documents) {
                            // Mapping firestore object to kotlin model
                            val discount: Discount = Discount(
                                document.id,
                                document.data?.get("title") as String,
                                document.data?.get("requiredPoint") as Long,
                                document.data?.get("description") as String,
                                document.data?.get("dateApplied") as String,
                                document.data?.get("dateExpired") as String,
                                document.data?.get("percent").toString().toDouble(),
                                document.data?.get("serviceApplied") as DocumentReference
                            )
                            // Insert to list if service is not deleted
                            if(document.data?.get("deleted") == null)
                                tmpDiscountList.add(discount)
                        }

                        // Call function to return appointment list after mapping complete
                        discountList.postValue(tmpDiscountList)
                    }
                }
        }
    }

    suspend fun saveDiscount(discount: HashMap<String, *>): Boolean {
        var ack: Boolean = false
        // Save service to database
        var tmp = HashMap(discount)

        tmp["serviceApplied"] = dbInstance!!.collection(Constant.collection.services).document(tmp["serviceApplied"] as String)

        try {
            if(dbInstance != null && discount != null) {
                dbInstance!!.collection(Constant.collection.discounts)
                    .add(tmp)
                    .await()

                ack = true
            }
        }
        catch (e: Exception) {
            Log.e("DbDiscountServices", "Error adding document", e)
        }

        return ack
    }

    suspend fun updateDiscount(discountId: String, discount: HashMap<String, *>): Boolean {
        var ack: Boolean = false
        // Save service to database
        try {
            if(dbInstance != null && discount != null) {
                dbInstance!!.collection(Constant.collection.discounts)
                    .document(discountId)
                    .set(discount, SetOptions.merge())
                    .await()

                ack = true
            }
        }
        catch (e: Exception) {
            Log.e("DbDiscountServices", "Error updating document", e)
        }

        return ack
    }

    suspend fun deleteDiscount(discountId: String): Boolean {
        var ack: Boolean = false
        // Add "deleted: true" to database
        try {
            if(dbInstance != null) {
                dbInstance!!.collection(Constant.collection.discounts)
                    .document(discountId)
                    .set(hashMapOf(
                        "deleted" to true
                    ), SetOptions.merge())
                    .await()

                ack = true
            }
        }
        catch (e: Exception) {
            Log.e("DbDiscountServices", "Error deleting document", e)
        }

        return ack
    }

    override suspend fun find(query: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun save(data: Any?): Any? {
        TODO("Not yet implemented")
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