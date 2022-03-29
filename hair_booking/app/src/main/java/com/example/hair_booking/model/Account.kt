package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Account(private val id: String) {
    private var username: String? = null
    private var password: String? = null
    private var role: String? = null
    private var banned: Boolean? = null

    // This is the attribute for manager only
    // With normal user, this attribute will be null
    private var hairSalon: DocumentReference? = null

    // Full parameter constructor for normal user
    constructor(id: String, username: String, password: String, role: String, banned: Boolean) : this(id) {
        this.username = username
        this.password = password
        this.role = role
        this.banned = banned
    }

    // Full parameter constructor for manager
    constructor(id: String, username: String, password: String, role: String, banned: Boolean, hairSalon: DocumentReference) : this(id) {
        this.username = username
        this.password = password
        this.role = role
        this.banned = banned
        this.hairSalon = hairSalon
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}