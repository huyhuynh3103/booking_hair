package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Account(private val _id: String) {
    private var _username: String? = null
    private var _password: String? = null
    private var _role: String? = null
    private var _banned: Boolean? = null

    // This is the attribute for manager only
    // With normal user, this attribute will be null
    private var _hairSalon: DocumentReference? = null


    // GETTERS
    val id: String = _id
    val username: String? = _username
    val password: String? = _password
    val role: String? = _role
    val banned: Boolean? = _banned
    val hairSalon: DocumentReference? = _hairSalon

    // Full parameter constructor for normal user
    constructor(id: String, username: String, password: String, role: String, banned: Boolean) : this(id) {
        this._username = username
        this._password = password
        this._role = role
        this._banned = banned
    }

    // Full parameter constructor for manager
    constructor(id: String, username: String, password: String, role: String, banned: Boolean, hairSalon: DocumentReference) : this(id) {
        this._username = username
        this._password = password
        this._role = role
        this._banned = banned
        this._hairSalon = hairSalon
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}