package com.example.hair_booking.utils

class PasswordValidator {
    companion object{
        @JvmStatic
        val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
        fun isPasswordValid(password:String):Boolean{
            return PASSWORD_REGEX.toRegex().matches(password)

        }
    }
}