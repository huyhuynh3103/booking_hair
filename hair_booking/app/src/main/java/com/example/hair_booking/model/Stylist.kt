package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Stylist(private val _id: String) {
    private var _fullName: String? = null
    private var _avatar: String? = null
    private var _description: String? = null
    private var _shifts: HashMap<String, HashMap<String, *>>? = null
    private var _workPlace: DocumentReference? = null
    private var _deleted: Boolean? = null

    // GETTERS
    val id: String get() = _id
    val fullName: String? get() = _fullName
    val avatar: String? get() = _avatar
    val description: String? get() = _description
    val shifts: HashMap<String, HashMap<String, *>>? get() = _shifts
    val workPlace: DocumentReference? get() = _workPlace
    val deleted: Boolean? get() = _deleted

    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        avatar: String,
        description: String,
        shifts: HashMap<String, HashMap<String, *>>?,
        workPlace: DocumentReference,
        deleted: Boolean
    ): this(id) {
        this._fullName = fullName
        this._avatar = avatar
        this._description = description
        this._shifts = shifts
        this._workPlace = workPlace
        this._deleted = deleted
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
    constructor(
        id: String,
        fullName: String,
        avatar: String,
        description: String
    ): this(id) {
        this._fullName = fullName
        this._avatar = avatar
        this._description = description
    }


    constructor(stylist: Stylist) : this(stylist._id) {
        this._fullName = stylist._fullName
        this._avatar = stylist._avatar
        this._description = stylist._description
        this._shifts = stylist._shifts
        this._workPlace = stylist._workPlace
    }
}