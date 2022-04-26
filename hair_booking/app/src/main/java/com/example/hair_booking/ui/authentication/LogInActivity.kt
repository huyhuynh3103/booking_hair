package com.example.hair_booking.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.*

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@LogInActivity
        handleSignUpBtn()
        handleLoginBtn()

    }

    private fun handleSignUpBtn() {
        binding.moveToSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
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
}