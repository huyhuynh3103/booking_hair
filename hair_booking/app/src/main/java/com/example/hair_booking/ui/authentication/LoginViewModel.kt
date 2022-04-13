package com.example.hair_booking.ui.authentication

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.services.auth.AuthRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginViewModel: ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password:LiveData<String> = _password

    val emailTextWatcher: TextWatcher
        get() = object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(emailEditable: Editable?) {
                _email.value = emailEditable.toString()
            }

        }
    val passwordTextWatcher: TextWatcher
        get() = object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(passwordEditable: Editable?) {
                _password.value = passwordEditable.toString()
            }

        }
    fun login(){
        Log.d("huy-login","login button clicked")
        if(_email.value==null||_password.value==null){

        }
        else{
            runBlocking {
                launch {
                    val taskLoginResult = async { AuthRepository.login(_email.value!!,_password.value!!) }
                    val task = taskLoginResult.await()
                    task.addOnCompleteListener { t->
                        if(t.isSuccessful){
                            Log.d("huy-login","success")
                        }
                        else{
                            Log.d("huy-login","failed")
                            Log.d("huy-login",t.exception?.message.toString())
                        }
                    }
                }
            }
        }

    }

}