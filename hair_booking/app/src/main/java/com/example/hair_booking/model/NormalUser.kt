package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class NormalUser(private val id: String) {
    private var fullName: String? = null
    private var discountPoint: Long? = null
    private var appointments: ArrayList<DocumentReference>?  = null
    private var wishList: ArrayList<DocumentReference>? = null



    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        discountPoint: Long,
        appointments: ArrayList<DocumentReference>,
        wishList: ArrayList<DocumentReference>
    ): this(id) {
        this.fullName = fullName
        this.discountPoint = discountPoint
        this.appointments = appointments
        this.wishList = wishList
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}