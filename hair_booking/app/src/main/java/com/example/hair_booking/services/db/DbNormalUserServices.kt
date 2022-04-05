package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.NormalUser
import com.google.firebase.firestore.FirebaseFirestore

class DbNormalUserServices(private var dbInstance: FirebaseFirestore?) {

    fun foo() {
        Log.d("xk", "adaldqlweql")
    }

    fun getNormalUserDetail(id: String): MutableLiveData<NormalUser> {
        var result = MutableLiveData<NormalUser>()

        if(dbInstance != null) {
            dbInstance!!.collection("normalUsers")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == id) {
                            // Mapping firestore object to kotlin model
                            var normalUser: NormalUser = NormalUser(
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
}