package com.example.hair_booking.services.db

import android.util.Log
import com.example.hair_booking.Constant
import com.example.hair_booking.model.Discount
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class DbDiscountServices(private var dbInstance: FirebaseFirestore?): DatabaseAbstract() {
    override suspend fun find(query: Any): Any {
        TODO("Not yet implemented")
    }

    override fun save(data: Any): Any {
        TODO("Not yet implemented")
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
                    document.data?.get("percent") as Long
                )

                discountList.add(discount)
            }

        }

        return discountList
    }

    override fun findById(data: Any): Any {
        TODO("Not yet implemented")
    }

    override fun updateOne(id: String, updateDoc: Any): Any {
        TODO("Not yet implemented")
    }

    override fun delete(data: Any): Any {
        TODO("Not yet implemented")
    }
}