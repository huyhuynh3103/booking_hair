package com.example.hair_booking.services.auth

import android.util.Log
import com.example.hair_booking.Constant
import com.example.hair_booking.firebase.Auth
import com.example.hair_booking.services.db.dbServices
import com.facebook.AccessToken
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    suspend fun loginByFacebook(token: AccessToken):AuthResult = coroutineScope {
        Log.d("facebook-login", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user
        if(user!=null){
            val query = hashMapOf(
                "email" to user.email!!
            )
            val res = async {
                dbServices.getAccountServices()!!.find(query)
            }.await()
            if(res.isEmpty()){
                val newAccount = hashMapOf(
                    "email" to user.email!!,
                    "role" to Constant.roles.userRole,
                    "banned" to false
                )

                val accountDocRef =
                    withContext(Dispatchers.Default) {
                        dbServices.getAccountServices()!!.save(newAccount)
                    }
                val newUser = hashMapOf(
                    "fullName" to user.displayName,
                    "gender" to "",
                    "accountId" to accountDocRef,
                    "phoneNumber" to "",
                    "discountPoint" to 0,
                    "appointment" to arrayListOf<DocumentReference>(),
                    "wishList" to arrayListOf<DocumentReference>()
                )
                // save new user
                dbServices.getNormalUserServices()!!.save(newUser)
            }
        }

        result
    }
    suspend fun loginByGoogle(idToken: String):AuthResult = coroutineScope{
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(firebaseCredential).await()
        val user = result.user
        if(user!=null){
            val query = hashMapOf(
                "email" to user.email!!
            )
            val res = async {
                dbServices.getAccountServices()!!.find(query)
            }.await()
            if(res.isEmpty()){
                val newAccount = hashMapOf(
                    "email" to user.email!!,
                    "role" to Constant.roles.userRole,
                    "banned" to false
                )

                val accountDocRef =
                    withContext(Dispatchers.Default) {
                        dbServices.getAccountServices()!!.save(newAccount)
                    }
                val newUser = hashMapOf(
                    "fullName" to user.displayName,
                    "gender" to "",
                    "accountId" to accountDocRef,
                    "phoneNumber" to "",
                    "discountPoint" to 0,
                    "appointment" to arrayListOf<DocumentReference>(),
                    "wishList" to arrayListOf<DocumentReference>()
                )
                // save new user
                dbServices.getNormalUserServices()!!.save(newUser)
            }
        }

        result
    }
    suspend fun createAccount(email: String, password: String): AuthResult {
        Log.d("huy-auth", "create account")
        return auth.createUserWithEmailAndPassword(email, password).await()
    }
    fun getCurrentUser():FirebaseUser? {
        return auth.currentUser
    }
    suspend fun updateProfile(name:String){
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        user!!.updateProfile(profileUpdates)
            .await()
    }
}