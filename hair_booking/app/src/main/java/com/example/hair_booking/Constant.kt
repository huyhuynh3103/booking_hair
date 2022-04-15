package com.example.hair_booking

object Constant {

     object collection {
        const val hairSalons: String = "hairSalons"
        const val accounts:String = "accounts"
        const val appointments:String = "appointments"
        const val discounts:String = "discounts"
        const val normalUsers:String = "normalUsers"
        const val services:String = "services"
        const val stylists:String = "stylists"
    }
    const val notFoundImg = "notfoundimg"
    object messages{
        const val required:String = "Required"
        const val invalidEmail:String = "Invalid Email"
        const val invalidPassword:String = "Invalid Password"
        const val loginFailed:String = "Invalid Email or Password. Try Again"
        const val notMatch:String = "Not match!"
        const val errorFromSever:String = "Something went wrong. Try later."
        const val length_password_condition_msg = "Must contains at least 8 characters."
        var passwordValidationMsg: ((String) -> String) = { condition ->
            "Must contains at least one $condition character."
        }
    }
}