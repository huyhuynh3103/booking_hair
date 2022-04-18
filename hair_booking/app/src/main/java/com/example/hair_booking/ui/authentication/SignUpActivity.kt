package com.example.hair_booking.ui.authentication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.hair_booking.Constant
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivitySignUpBinding
import com.example.hair_booking.services.auth.AuthRepository
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.*
import java.lang.Exception


class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@SignUpActivity

        handleSignUpBtn()
    }

    @DelicateCoroutinesApi
    private fun handleSignUpBtn() {
        binding.signUpBtn.setOnClickListener {
            Log.d("huy-register", "Sign up button clicked")
//           check empty
            var con = true
            val fullName = binding.nameSignUpET.text.toString()
            if (fullName.isEmpty()) {
                binding.viewModel?.setFullNameWarningVisible()
                con = false
            }

            val email = binding.emailSignUpET.text.toString()
            if (email.isEmpty()) {
                binding.viewModel?.setEmailWarningVisible()
                con = false
            }

            val password = binding.passwordSignUpET.text.toString()
            if (password.isEmpty()) {
                binding.viewModel?.setPasswordWarningVisible()
                con = false
            }

            val confirmPwd = binding.confirmPasswordET.text.toString()
            if (confirmPwd.isEmpty()) {
                binding.viewModel?.setConfirmWarningVisible()
                con = false
            }

            val idGenderChecked = binding.genderGroupBtn.checkedRadioButtonId
            var gender = binding.maleRadioBtn.text.toString()
            if (idGenderChecked != R.id.maleRadioBtn) {
                gender = binding.femaleRadioBtn.text.toString()
            }
            val phoneNumber: String = binding.phoneNumberET.text.toString()
            if (con) {
                Log.d("huy-sign-up", "check empty field: passed")


                GlobalScope.launch {
                    try {
                        // save to fire base auth
                        val result = async { AuthRepository.createAccount(email, password) }
                        val profile = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()

                        result.await().user?.updateProfile(profile)
                        // save new account
                        val newAccount = hashMapOf(
                            "email" to email,
                            "role" to Constant.roles.userRole,
                            "banned" to false
                        )

                        val accountDocRef =
                            withContext(Dispatchers.Default) {
                                dbServices.getAccountServices()!!.save(newAccount)
                            }
                        val newUser = hashMapOf(
                            "fullName" to fullName,
                            "gender" to gender,
                            "accountId" to accountDocRef,
                            "phoneNumber" to phoneNumber,
                            "discountPoint" to 0,
                            "appointment" to arrayListOf<DocumentReference>(),
                            "wishList" to arrayListOf<DocumentReference>()

                        )
                        // save new user
                        dbServices.getNormalUserServices()!!.save(newUser)
                        runOnUiThread { Toast.makeText(applicationContext,
                            Constant.messages.signUpSuccess,
                            Toast.LENGTH_LONG).show() }
                        finish()


                    } catch (e: FirebaseAuthWeakPasswordException) {
                        runOnUiThread {
                            Toast.makeText(applicationContext,
                                Constant.messages.signUpFailedByWeekPassword,
                                Toast.LENGTH_LONG).show()

                        }
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        runOnUiThread {
                            Toast.makeText(applicationContext,
                                Constant.messages.signUpFailedByMalformedEmail,
                                Toast.LENGTH_LONG).show()
                        }
                    } catch (e: FirebaseAuthUserCollisionException) {
                        runOnUiThread {
                            Toast.makeText(applicationContext,
                                Constant.messages.signUpFailedByExistsEmail,
                                Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e: Exception){
                        Log.e("huy-sign-up","Exception occurs: ${e.message}")
                    }

                }
            }


        }
    }
}