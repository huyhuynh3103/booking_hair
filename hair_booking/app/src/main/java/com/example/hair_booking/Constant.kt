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
        const val shifts: String = "shifts"
    }

    object AppointmentStatus {
        val accept: String = "Chấp nhận"
        val deny: String = "Từ chối"
    }
    const val notFoundImg = "notfoundimg"
    object messages{
        const val required:String = "Required"
        const val invalidEmail:String = "Invalid Email"
        const val loginFailedByEmail:String = "The email does not exist or has been disabled. Try Again"
        const val loginFailedByPassword:String = "The wrong password. Try Again"
        const val signUpFailedByWeekPassword:String = "Week password! Try Again"
        const val signUpFailedByMalformedEmail:String = "Malformed email! Try Again"
        const val signUpFailedByExistsEmail:String = "Exists email! Try Again"
        const val signUpSuccess:String = "Sign Up Successfully."
        const val notMatch:String = "Not match!"
        const val errorFromSever:String = "Something went wrong. Try later."
        const val bannedAccount: String = "This account has been banned by admin. Contact us for any question."
        const val length_password_condition_msg = "Must contains at least 8 characters."
        var passwordValidationMsg: ((String) -> String) = { condition ->
            "Must contains at least one $condition character."
        }
    }
    object roles{
        const val userRole:String = "user"
        const val managerRole:String = "manager"
    }
}