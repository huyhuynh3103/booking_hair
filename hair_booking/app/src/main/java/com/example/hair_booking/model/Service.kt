package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Service(private val id: String) {
    private var title: String? = null
    private var price: Long? = null
    private var description: String? = null

    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        price: Long,
        description: String
    ): this(id) {
        this.title = fullName
        this.price = price
        this.description = description
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}