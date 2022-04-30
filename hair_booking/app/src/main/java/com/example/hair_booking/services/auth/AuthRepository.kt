package com.example.hair_booking.services.auth

import android.util.Log
import com.example.hair_booking.firebase.Auth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth: FirebaseAuth = Auth.getInstance()
    fun isSignIn():Boolean{
        if(auth.currentUser!=null){
            return true
        }
        return false
    }
    fun signOut(){
        auth.signOut()
    }
    suspend fun login( email:String,  password:String): AuthResult{
        Log.d("huy-auth", "login account")
        var result: AuthResult?
        try{
            result = auth.signInWithEmailAndPassword(email, password)
                .await()
        }catch (e: Exception){
            throw e
        }

        return  result
    }
    suspend fun loginByGoogle(idToken: String):AuthResult{
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(firebaseCredential).await()
        return result
    }
    suspend fun createAccount(email: String, password: String): AuthResult {
        Log.d("huy-auth", "create account")
        return auth.createUserWithEmailAndPassword(email, password).await()
    }
    fun getCurrentUser():FirebaseUser? {
        return auth.currentUser
    }
}