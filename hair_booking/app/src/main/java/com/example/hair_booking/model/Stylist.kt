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
    private var _workPlace: DocumentReference? = null
    private var _shift: HashMap<String, HashMap<*, *>>? = null
    private var _deleted: Boolean? = null

    // GETTERS
    val id: String get() = _id
    val fullName: String? get() = _fullName
    val avatar: String? get() = _avatar
    val description: String? get() = _description
    val workPlace: DocumentReference? get() = _workPlace
    val shift: HashMap<String, HashMap<*, *>>? get() = _shift
    val deleted: Boolean? get() = _deleted

    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        avatar: String,
        description: String,
        workPlace: DocumentReference,
        shift: HashMap<String, HashMap<*, *>>,
        deleted: Boolean
    ): this(id) {
        this._fullName = fullName
        this._avatar = avatar
        this._description = description
        this._workPlace = workPlace
        this._shift = shift
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
}