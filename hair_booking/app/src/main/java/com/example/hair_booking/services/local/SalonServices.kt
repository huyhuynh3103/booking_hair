package com.example.hair_booking.services.local

class SalonServices {
    companion object {
        // Concatenate parts of address into 1 string
        fun addressToString(address: HashMap<String, *>): String {
            if(address != null) {
                return address!!["streetNumber"] as String + " " + address!!["streetName"] + ", " + address!!["ward"] + ", " + address!!["district"] + ", " + address!!["city"]
            }
            return ""
        }
    }
}