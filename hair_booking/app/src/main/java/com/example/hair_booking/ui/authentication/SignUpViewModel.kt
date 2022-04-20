package com.example.hair_booking.ui.authentication

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.hair_booking.Constant
import com.example.hair_booking.utils.EmailValidator
import com.example.hair_booking.utils.PasswordValidator

class SignUpViewModel: ViewModel() {
    private val _password = MutableLiveData<String>()

    // email
    private val _isVisibleEmailWarning = MutableLiveData<Boolean>()
    val isVisibleEmailWarning:LiveData<Boolean> = _isVisibleEmailWarning
    private val _emailWarning = MutableLiveData<String>()
    val emailWarning:LiveData<String> = _emailWarning
    fun setEmailWarningVisible(){
        _isVisibleEmailWarning.value = true
    }

    // full name
    private val _isVisibleFullNameWarning = MutableLiveData<Boolean>()
    val isVisibleFullNameWarning:LiveData<Boolean> = _isVisibleFullNameWarning
    private val _fullNameWarning = MutableLiveData<String>()
    val fullNameWarning:LiveData<String> = _fullNameWarning

    fun setFullNameWarningVisible(){
        _isVisibleFullNameWarning.value = true
    }
    // password
    private val _isVisiblePasswordWarning = MutableLiveData<Boolean>()
    val isVisiblePasswordWarning:LiveData<Boolean> = _isVisiblePasswordWarning
    private val _passwordWarning = MutableLiveData<String>()
    val passwordWarning:LiveData<String> = _passwordWarning

    fun setPasswordWarningVisible(){
        _isVisiblePasswordWarning.value = true
    }


    //confirm password
    private val _isVisibleConfirmPasswordWarning = MutableLiveData<Boolean>()
    val isVisibleConfirmPasswordWarning:LiveData<Boolean> = _isVisibleConfirmPasswordWarning
    private val _passwordConfirmationWarning = MutableLiveData<String>()
    val passwordConfirmationWarning:LiveData<String> = _passwordConfirmationWarning

    fun setConfirmWarningVisible(){
        _isVisibleConfirmPasswordWarning.value = true
    }

    init {
        _isVisibleEmailWarning.value = false
        _emailWarning.value = Constant.messages.required
        _isVisibleFullNameWarning.value = false
        _fullNameWarning.value = Constant.messages.required
        _isVisiblePasswordWarning.value = false
        _passwordWarning.value=Constant.messages.required
        _isVisibleConfirmPasswordWarning.value = false
        _passwordConfirmationWarning.value = Constant.messages.required

    }
//    Text watcher for email
    val emailTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(emailEditable: Editable?) {
                val email = emailEditable.toString()
                when {
                    email.isEmpty() -> {
                        _isVisibleEmailWarning.value = true
                        _emailWarning.value = Constant.messages.required
                    }
                    !EmailValidator.isEmailValid(email) -> {
                        _isVisibleEmailWarning.value = true
                        _emailWarning.value = Constant.messages.invalidEmail
                    }
                    else -> {
                        _isVisibleEmailWarning.value = false
                        _emailWarning.value = ""
                    }
                }
            }
        }

//    Text watcher for full name
    val fullNameTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(fullNameEditable: Editable?) {
                val fullName = fullNameEditable.toString()
                when {
                    fullName.isEmpty() -> {
                        _isVisibleFullNameWarning.value = true
                        _fullNameWarning.value = Constant.messages.required
                    }
                    else -> {
                        _isVisibleFullNameWarning.value = false
                        _fullNameWarning.value = ""
                    }
                }
            }
        }

//    Text watcher for password
    val passwordTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(password: CharSequence?, p1: Int, p2: Int, p3: Int) {
                password?.let {
                    val pwd = it.toString()
                    when {
                        pwd.isEmpty() -> {
                            _isVisiblePasswordWarning.value = true
                            _passwordWarning.value = Constant.messages.required
                        }
                        PasswordValidator.validate(pwd).isNotEmpty() ->{
                            _isVisiblePasswordWarning.value = true
                            _passwordWarning.value = ""
                            val messages = PasswordValidator.validate(pwd)
                            for(index in messages.indices){
                                if(index==messages.lastIndex)
                                {
                                    _passwordWarning.value+= messages[index]
                                }
                                else{
                                    _passwordWarning.value+=messages[index]+"\n"
                                }
                            }
                        }
                        else -> {
                            _isVisiblePasswordWarning.value = false
                            _passwordWarning.value = ""
                        }
                    }
                }

            }

            override fun afterTextChanged(passwordEditable: Editable?) {
                val password = passwordEditable.toString()
                _password.value = password

            }
        }

//    Text watcher for confirmation password
    val confirmPasswordTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(confirmPwdEditable: Editable?) {
                val confirmPwd = confirmPwdEditable.toString()
                when{
                    confirmPwd.isEmpty() -> {
                        _isVisibleConfirmPasswordWarning.value = true
                        _passwordConfirmationWarning.value = Constant.messages.required
                    }
                    confirmPwd != _password.value -> {
                        _isVisibleConfirmPasswordWarning.value = true
                        _passwordConfirmationWarning.value = Constant.messages.notMatch
                    }
                    else -> {
                        _isVisibleConfirmPasswordWarning.value = false
                        _passwordConfirmationWarning.value = ""

                    }
                }
            }

        }
}