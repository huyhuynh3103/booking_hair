package com.example.hair_booking.utils

import com.example.hair_booking.Constant

class PasswordValidator {
    companion object{
        @JvmStatic
        val PASSWORD_REGEXES = mapOf(
            "length" to "^.{8,}$",
            "number" to ".*[0-9]",
            "lowercase" to ".*[a-z]",
            "uppercase" to ".*[A-Z]",
            "special" to ".*[#?!@\$%^&*-]"
        )
        fun validate(password:String): List<String> {
            val result_messages = arrayListOf<String>()
            for (condition in PASSWORD_REGEXES.keys){
                val regex = PASSWORD_REGEXES.get(condition)?.toRegex()
                regex?.let { r ->
                    if(!r.containsMatchIn(password)){
                        if(condition=="length"){
                            result_messages.add(Constant.messages.length_password_condition_msg)
                        }
                        else{
                            result_messages.add(Constant.messages.passwordValidationMsg(condition))
                        }
                    }
                }
            }
            return result_messages
        }

    }
}