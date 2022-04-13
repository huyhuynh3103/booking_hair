package com.example.hair_booking.services.auth

import com.example.hair_booking.firebase.Auth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object AuthRepository {
    private val auth: FirebaseAuth = Auth.getInstance()
    fun checkLogin():Boolean{
        if(auth.currentUser!=null){
            return true
        }
        return false
    }
    suspend fun login( email:String,  password:String): Task<AuthResult> = coroutineScope{
        val result = async { auth.signInWithEmailAndPassword(email, password) }
        result.await()
    }
    fun createAccount(email:String,password: String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    // create new account successful
                }
                else{
                    // create new account failed

                }
            }
    }
    fun getCurrentUser():FirebaseUser?{
        return auth.currentUser
    }
}