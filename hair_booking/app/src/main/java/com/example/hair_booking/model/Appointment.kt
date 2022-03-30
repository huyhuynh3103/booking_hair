package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Appointment(private val _id: String) {
    private var _userId: DocumentReference? = null
    private var _userFullName: String? = null
    private var _userPhoneNumber: String? = null

    // "*" data type is used to deal with map in map in firestore
    private var _hairSalon: HashMap<String, *>?  = null

    private var _service: HashMap<String, *>? = null
    private var _stylist: HashMap<String, *>? = null

    private var _bookingDate: String? = null
    private var _bookingTime: String? = null
    private var _createdAt: String? = null
    private var _discountApplied: HashMap<String, *>? = null

    private var _note: String? = null
    private var _status: String? = null


    // GETTERS
    val id: String get() = _id
    val userId: DocumentReference? get() = _userId
    val userFullName: String? get() = _userFullName
    val userPhoneNumber: String? get() = _userPhoneNumber

    // "*" data type is used to deal with map in map in firestore
    val hairSalon: HashMap<String, *>?  get() = _hairSalon

    val service: HashMap<String, *>? get() = _service
    val stylist: HashMap<String, *>? get() = _stylist

    val bookingDate: String? get() = _bookingDate
    val bookingTime: String? get() = _bookingTime
    val createdAt: String? get() = _createdAt
    val discountApplied: HashMap<String, *>? get() = _discountApplied

    val note: String? get() = _note
    val status: String? get() = _status


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
        this._userId = userId
        this._userFullName = userFullName
        this._userPhoneNumber = userPhoneNumber
        this._hairSalon = hairSalon
        this._service = service
        this._stylist = stylist
        this._bookingDate = bookingDate
        this._bookingTime = bookingTime
        this._createdAt = createdAt
        this._discountApplied = discountApplied
        this._note = note
        this._status = status
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}