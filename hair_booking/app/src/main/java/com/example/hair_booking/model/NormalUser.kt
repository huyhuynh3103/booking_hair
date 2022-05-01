package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class NormalUser(private val _id: String) {
    private var _fullName: String? = null
    private var _phoneNumber: String? = null
    private var _gender: String? = null
    private var _discountPoint: Long? = null
    private var _appointments: ArrayList<HashMap<String, *>>?  = null
    private var _wishList: ArrayList<DocumentReference>? = null
    private var _accountId: DocumentReference? = null
    private var _username: String? = null
    private var _banned: Boolean? = null


    // GETTERS
    val id: String get() = _id
    val fullName: String? get() = _fullName
    val phoneNumber: String? get() = _phoneNumber
    val gender: String? get() = _gender
    val discountPoint: Long? get() = _discountPoint
    val appointments: ArrayList<HashMap<String, *>>? get() = _appointments
    val wishList: ArrayList<DocumentReference>? get() = _wishList
    val accountId: DocumentReference? get() = _accountId
    val username: String? = _username
    val banned: Boolean? = _banned

    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        phoneNumber: String,
        gender: String,
        discountPoint: Long,
        appointments: ArrayList<HashMap<String, *>>,
        wishList: ArrayList<DocumentReference>,
        accountId: DocumentReference
    ): this(id) {
        this._fullName = fullName
        this._phoneNumber = phoneNumber
        this._gender = gender
        this._discountPoint = discountPoint
        this._appointments = appointments
        this._wishList = wishList
        this._accountId = accountId
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
    // Constructor for User Wishlist
    constructor(
        id: String,
        fullName: String,
        phoneNumber: String,
        gender: String,
        discountPoint: Long,
        wishList: ArrayList<DocumentReference>,
        accountId: DocumentReference
    ): this(id) {
        this._fullName = fullName
        this._phoneNumber = phoneNumber
        this._gender = gender
        this._discountPoint = discountPoint
        this._wishList = wishList
        this._accountId = accountId
    }

    // constructor for normal user detail screen
    constructor(
        id: String,
        fullName: String,
        phoneNumber: String,
        gender: String,
        discountPoint: Long,
        accountId: DocumentReference
    ): this(id) {
        this._fullName = fullName
        this._phoneNumber = phoneNumber
        this._gender = gender
        this._discountPoint = discountPoint
        this._accountId = accountId
    }

    constructor(
        id: String,
        fullName: String,
        phoneNumber: String,
        gender: String,
        discountPoint: Long
    ): this(id) {
        this._fullName = fullName
        this._phoneNumber = phoneNumber
        this._gender = gender
        this._discountPoint = discountPoint
    }

}