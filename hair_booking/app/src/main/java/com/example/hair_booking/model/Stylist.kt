package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Stylist(private val id: String) {
    private var _fullName: String? = null
    private var _avatar: String? = null
    private var _description: String? = null
    private var _workPlace: DocumentReference? = null

    // GETTERS
    val fullName: String? = _fullName
    val avatar: String? = _avatar
    val description: String? = _description
    val workPlace: DocumentReference? = _workPlace

    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        avatar: String,
        description: String,
        workPlace: DocumentReference
    ): this(id) {
        this._fullName = fullName
        this._avatar = avatar
        this._description = description
        this._workPlace = workPlace
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}