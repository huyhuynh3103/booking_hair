package com.example.hair_booking.services.local

import com.google.firebase.firestore.DocumentReference

class StylistServices {
    companion object {
        fun isWorking(shiftIdToBeChecked: String, shifts: HashMap<*, *>): Boolean {
            val morningShiftId: String = ((shifts["morning"] as HashMap<String, *>)["id"] as DocumentReference).id
            val isWorkingInTheMorning: Boolean = (shifts["morning"] as HashMap<String, *>)["isWorking"] as Boolean
            if(morningShiftId == shiftIdToBeChecked && isWorkingInTheMorning)
                return true

            val afternoonShiftId: String = ((shifts["afternoon"] as HashMap<String, *>)["id"] as DocumentReference).id
            val isWorkingInTheAfternoon: Boolean = (shifts["afternoon"] as HashMap<String, *>)["isWorking"] as Boolean
            if(afternoonShiftId == shiftIdToBeChecked && isWorkingInTheAfternoon)
                return true

            val eveningShiftId: String = ((shifts["evening"] as HashMap<String, *>)["id"] as DocumentReference).id
            val isWorkingInTheEvening: Boolean = (shifts["evening"] as HashMap<String, *>)["isWorking"] as Boolean
            if(eveningShiftId == shiftIdToBeChecked && isWorkingInTheEvening)
                return true

            return false
        }
    }
}