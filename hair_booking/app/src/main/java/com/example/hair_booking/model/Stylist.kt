package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Stylist(private val id: String) {
    private var fullName: String? = null
    private var avatar: String? = null
    private var description: String? = null
    private var workPlace: DocumentReference? = null

    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        avatar: String,
        description: String,
        workPlace: DocumentReference
    ): this(id) {
        this.fullName = fullName
        this.avatar = avatar
        this.description = description
        this.workPlace = workPlace
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}