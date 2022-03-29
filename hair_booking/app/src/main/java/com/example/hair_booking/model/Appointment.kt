package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Appointment(private val id: String) {
    private var userId: DocumentReference? = null
    private var userFullName: String? = null
    private var userPhoneNumber: String? = null

    // "*" data type is used to deal with map in map in firestore
    private var hairSalon: HashMap<String, *>?  = null

    private var service: HashMap<String, *>? = null
    private var stylist: HashMap<String, *>? = null

    private var bookingDate: String? = null
    private var bookingTime: String? = null
    private var createdAt: String? = null
    private var discountApplied: HashMap<String, *>? = null

    private var note: String? = null
    private var status: String? = null

    // Full parameter constructor
    constructor(id: String, userId: DocumentReference,
                userFullName: String,
                userPhoneNumber: String?,
                hairSalon: HashMap<String, *>,
                service: HashMap<String, *>,
                stylist: HashMap<String, *>,
                bookingDate: String,
                bookingTime: String,
                createdAt: String,
                discountApplied: HashMap<String, *>,
                note: String,
                status: String
    ): this(id) {
        this.userId = userId
        this.userFullName = userFullName
        this.userPhoneNumber = userPhoneNumber
        this.hairSalon = hairSalon
        this.service = service
        this.stylist = stylist
        this.bookingDate = bookingDate
        this.bookingTime = bookingTime
        this.createdAt = createdAt
        this.discountApplied = discountApplied
        this.note = note
        this.status = status
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}