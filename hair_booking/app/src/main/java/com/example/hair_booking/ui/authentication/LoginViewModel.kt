package com.example.hair_booking.ui.authentication

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hair_booking.Constant
import com.example.hair_booking.utils.EmailValidator

class LoginViewModel: ViewModel() {

    private val _isVisibleUsrMsg = MutableLiveData<Boolean>()
    val isVisbileUsrMsg:LiveData<Boolean> = _isVisibleUsrMsg

    private val _usrMsg = MutableLiveData<String>()
    val usrMsg:LiveData<String> = _usrMsg

    private val _pwdMsg = MutableLiveData<String>()
    val pwdMsg:LiveData<String> = _pwdMsg

    private val _isVisiblePwdMsg = MutableLiveData<Boolean>()
    val isVisbilePwdMsg:LiveData<Boolean> = _isVisiblePwdMsg

    init {
        _usrMsg.value = ""
        _pwdMsg.value = ""
        _isVisibleUsrMsg.value = false
        _isVisiblePwdMsg.value = false
    }
    fun validateEmail(email:String):Boolean{
        val res:Boolean
        when {
            email.isEmpty() -> {
                _isVisibleUsrMsg.value = true
                _usrMsg.value = Constant.messages.required
                res = false
            }
            !EmailValidator.isEmailValid(email) -> {
                _isVisibleUsrMsg.value = true
                _usrMsg.value = Constant.messages.invalidEmail
                res =false
            }
            else -> {
                _isVisiblePwdMsg.value = false
                _usrMsg.value = ""
                res = true
            }
        }
        return res
    }
    fun validatePwd(password:String):Boolean{
        if (password.isEmpty()){
            _isVisiblePwdMsg.value = true
            _pwdMsg.value = Constant.messages.required
            return false
        }
        else{
            _isVisiblePwdMsg.value = false
            _pwdMsg.value = ""
            return true
        }
    }
    val emailTextWatcher: TextWatcher
        get() = object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(emailEditable: Editable?) {
                val email = emailEditable.toString()
                validateEmail(email)
            }

        }
    val passwordTextWatcher: TextWatcher
        get() = object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(passwordEditable: Editable?) {
                val password = passwordEditable.toString()
                validatePwd(password)
            }

        }

}