package com.example.hair_booking.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityLogInBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.ui.normal_user.home.NormalUserHomeActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private val viewModel:LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_log_in)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@LogInActivity
        handleSignUpBtn()
        handleLoginBtn()

    }
    private fun handleSignUpBtn(){
        binding.moveToSignUpButton.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun handleLoginBtn(){
        binding.loginButton.setOnClickListener {
            Log.d("huy-login","login button clicked")
            val email = binding.emailTV.text.toString()
            val password = binding.passwordTV.text.toString()
            runBlocking {
                launch {
                    val taskLoginResult = async { AuthRepository.login(email,password) }
                    val task = taskLoginResult.await()
                    task.addOnCompleteListener { t->
                        if(t.isSuccessful){
                            Log.d("huy-login","success")
                            val intent = Intent(applicationContext,NormalUserHomeActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Log.d("huy-login","failed")
                            Log.d("huy-login",t.exception?.message.toString())
                            Toast.makeText(applicationContext,Constant.messages.loginFailed,Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }


        }
    }
}