package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class NormalUser(private val _id: String) {
    private var _fullName: String? = null
    private var _discountPoint: Long? = null
    private var _appointments: ArrayList<DocumentReference>?  = null
    private var _wishList: ArrayList<DocumentReference>? = null


    // GETTERS
    val id: String = _id
    val fullName: String? = _fullName
    val discountPoint: Long? = _discountPoint
    val appointments: ArrayList<DocumentReference>?  = _appointments
    val wishList: ArrayList<DocumentReference>? = _wishList

    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        discountPoint: Long,
        appointments: ArrayList<DocumentReference>,
        wishList: ArrayList<DocumentReference>
    ): this(id) {
        this._fullName = fullName
        this._discountPoint = discountPoint
        this._appointments = appointments
        this._wishList = wishList
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}