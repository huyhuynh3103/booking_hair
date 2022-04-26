package com.example.hair_booking.ui.authentication

import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityLogInBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.ui.admin.home.AdminHomeActivity
import com.example.hair_booking.ui.manager.home.ManagerHomeActivity
import com.example.hair_booking.ui.normal_user.home.NormalUserHomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.*

class LogInActivity : AppCompatActivity() {
    companion object {
        private const val RC_SIGN_IN = 9001
    }
    private lateinit var binding: ActivityLogInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private var showOneTapUI = true
    private val viewModel: LoginViewModel by viewModels()
    override fun onStart() {
        super.onStart()
//        if(AuthRepository.isSignIn()){
//            val currentUser =AuthRepository.getCurrentUser()
//            val email = currentUser?.email
//            if(email != null){
//                runBlocking {
//                    navigateToLandingPage(email)
//                }
//            }
//            else{
//                Toast.makeText(applicationContext,Constant.messages.errorFromSever,Toast.LENGTH_LONG).show()
//            }
//        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@LogInActivity
        binding.passwordTV.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        handleSignUpBtn()
        handleLoginBtn()

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_api_key))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]


        handleGoogleLogin()
        handleFacebookLogin()
    }

    private fun handleSignUpBtn() {
        binding.moveToSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun handleGoogleLogin(){

        binding.googleBtn.setOnClickListener {
            Log.d("google-login","google clicked")
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    private fun handleFacebookLogin(){
        binding.fbBtn.setOnClickListener {

            Log.d("facebook-login","facebook clicked")
        }
    }
    private fun handleLoginBtn() {
        binding.loginButton.setOnClickListener {
            Log.d("huy-login", "login button clicked")
            val email = binding.emailTV.text.toString()
            val password = binding.passwordTV.text.toString()
            val isValidateEmail = viewModel.validateEmail(email)
            val isValidatePassword = viewModel.validatePwd(password)
            if(isValidateEmail&&isValidatePassword){
                viewModel.viewModelScope.launch {
                    try {
                        binding.progressBarLogin.visibility = View.VISIBLE
                        AuthRepository.login(email, password)
                        navigateToLandingPage(email)
                        binding.progressBarLogin.visibility = View.INVISIBLE
                    } catch (e: FirebaseAuthInvalidUserException) {
                        binding.progressBarLogin.visibility = View.INVISIBLE
                        Log.d("Login Failed",Constant.messages.loginFailedByEmail)
                        runOnUiThread{
                            Toast.makeText(applicationContext,
                                Constant.messages.loginFailed,
                                Toast.LENGTH_LONG).show()
                        }
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        binding.progressBarLogin.visibility = View.INVISIBLE
                        Log.d("Login Failed",Constant.messages.loginFailedByPassword)
                        runOnUiThread {
                            Toast.makeText(applicationContext,
                                Constant.messages.loginFailed,
                                Toast.LENGTH_LONG).show()
                        }
                    }


                }
            }
        }
    }
    private suspend fun navigateToLandingPage(email:String) = coroutineScope{
        val query = hashMapOf(
            "email" to email
        )
        val accountResult = async { dbServices.getAccountServices()!!.find(query) }.await()
        if (accountResult.isNotEmpty()) {
            if (accountResult[0].banned != true) {
                if (accountResult[0].role == Constant.roles.userRole) {
                    val intent = Intent(applicationContext,
                        NormalUserHomeActivity::class.java)
                    startActivity(intent)
                }
                else if(accountResult[0].role == Constant.roles.managerRole) {
                    val intent =
                        Intent(applicationContext, ManagerHomeActivity::class.java)
                    startActivity(intent)
                }
                else if(accountResult[0].role == Constant.roles.adminRole){
                    val intent =
                        Intent(applicationContext,AdminHomeActivity::class.java)
                    startActivity(intent)
                }
                else{
                    // Undefined role
                    Log.d("huy-login", "undefined role")
                }
            } else {
                Log.d("huy-login", "banned")
                runOnUiThread {
                    Toast.makeText(applicationContext,
                        Constant.messages.bannedAccount,
                        Toast.LENGTH_LONG).show()
                }

            }
        } else {
            Log.d("huy-login", "No account in firestore")
            runOnUiThread{
                Toast.makeText(applicationContext,Constant.messages.errorFromSever,Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.id
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            Log.d("google-login", "Got ID token.")
                            viewModel.viewModelScope.launch{
                                try{
                                    binding.progressBarLogin.visibility = View.VISIBLE
                                    AuthRepository.loginByGoogle(idToken)
                                    val email = AuthRepository.getCurrentUser()?.email
                                    if(email!=null){
                                        navigateToLandingPage(email)
                                    }
                                    binding.progressBarLogin.visibility = View.INVISIBLE
                                    Log.d("google-login", "signInWithCredential:success")
                                }catch (e:FirebaseAuthInvalidUserException){
                                    runOnUiThread{
                                        Toast.makeText(applicationContext,
                                            Constant.messages.loginCredentialInvalid,
                                            Toast.LENGTH_LONG).show()
                                    }
                                }catch (e:FirebaseAuthInvalidCredentialsException){
                                    runOnUiThread{
                                        Toast.makeText(applicationContext,
                                            Constant.messages.loginCredentialsExpired,
                                            Toast.LENGTH_LONG).show()
                                    }
                                }catch (e: FirebaseAuthUserCollisionException){
                                    runOnUiThread{
                                        Toast.makeText(applicationContext,Constant.messages.loginCredentialsCollision,
                                            Toast.LENGTH_LONG).show()
                                    }
                                }

                            }

                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("google-login", "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d("google-login", "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d("google-login", "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d("google-login", "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                        }
                    }
                }
            }
        }
    }
}