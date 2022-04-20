package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Shift(private val _id: String) {
    private var _type: String? = null
    private var _startHour: String? = null
    private var _endHour: String? = null
    // GETTERS
    val id: String get() = _id
    val type: String? get() = _type
    val startHour: String? get() = _startHour
    val endHour: String? get() = _endHour

    // Full parameter constructor
    constructor(
        id: String,
        type: String,
        startHour: String,
        endHour: String
    ): this(id) {
        this._type = type
        this._startHour = startHour
        this._endHour = endHour
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW


    fun toStringForBookingDisplay(): String {
        val startHourToDisplay = _startHour?.replace('.', ':')
        val endHourToDisplay = _endHour?.replace('.', ':')
        return "$_type ($startHourToDisplay - $endHourToDisplay)"
    }
}