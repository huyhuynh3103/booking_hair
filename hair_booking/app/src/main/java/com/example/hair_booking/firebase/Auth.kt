package com.example.hair_booking.firebase

import com.google.firebase.auth.FirebaseAuth
object Auth {
    private var _instance: FirebaseAuth? = null
    fun getInstance():FirebaseAuth{
        if (_instance!=null){
            return _instance as FirebaseAuth
        }
        _instance = FirebaseAuth.getInstance()
        return _instance as FirebaseAuth
    }
}