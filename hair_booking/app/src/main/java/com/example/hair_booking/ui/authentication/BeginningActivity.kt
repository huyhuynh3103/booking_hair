package com.example.hair_booking.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityBeginingBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.ui.admin.home.AdminHomeActivity
import com.example.hair_booking.ui.manager.home.ManagerHomeActivity
import com.example.hair_booking.ui.normal_user.home.NormalUserHomeActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class BeginningActivity : AppCompatActivity() {
    lateinit var binding : ActivityBeginingBinding
    override fun onStart() {
        super.onStart()
        if(AuthRepository.isSignIn()){
            val currentUser = AuthRepository.getCurrentUser()
            val email = currentUser?.email
            if(email != null){
                lifecycleScope.launch {
                    navigateToLandingPage(email)
                }
            }
            else{
                Toast.makeText(applicationContext, Constant.messages.errorFromSever, Toast.LENGTH_LONG).show()
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                else if(accountResult[0].role == Constant.roles.managerRole) {
                    val intent =
                        Intent(applicationContext, ManagerHomeActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                else if(accountResult[0].role == Constant.roles.adminRole){
                    val intent =
                        Intent(applicationContext, AdminHomeActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_begining)
        binding.getStartedBtn.setOnClickListener {
            val intent = Intent(this,LogInActivity::class.java)
            startActivity(intent)
        }
    }
}