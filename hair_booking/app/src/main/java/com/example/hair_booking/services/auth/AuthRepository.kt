package com.example.hair_booking.services.auth

import android.util.Log
import com.example.hair_booking.firebase.Auth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth: FirebaseAuth = Auth.getInstance()
    fun checkLogin():Boolean{
        if(auth.currentUser!=null){
            return true
        }
        return false
    }
    suspend fun login( email:String,  password:String): Task<AuthResult> = coroutineScope{
        Log.d("huy-auth", "login account")
        val result = async { auth.signInWithEmailAndPassword(email, password) }
        result.await()
    }
    suspend fun createAccount(email:String,password: String): AuthResult?{
        Log.d("huy-auth", "create account")
        var result: AuthResult? = null
        try {
            auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    result = it
                    Log.e("huy-sign-up","create account success")
                }
                .addOnCanceledListener{
                    result = null
                    Log.e("huy-sign-up","create account failed")
                }

                .await()
        }catch (e:Exception){
            throw e
        }
        return result
    }
    suspend fun getCurrentUser():FirebaseUser? = coroutineScope{
        val result = async { auth.currentUser }
        result.await()
    }
}