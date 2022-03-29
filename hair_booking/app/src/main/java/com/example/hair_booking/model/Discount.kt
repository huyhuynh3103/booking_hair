package com.example.hair_booking.model

import com.google.firebase.firestore.DocumentReference

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Discount(private val id: String) {
    private var title: String? = null
    private var requiredPoint: Long? = null
    private var description: String? = null
    private var dateApplied: String? = null
    private var dateExpired: String? = null


    // Full parameter constructor
    constructor(id: String,
                title: String,
                requiredPoint: Long,
                description: String,
                dateApplied: String,
                dateExpired: String
    ) : this(id) {
        this.title = title
        this.requiredPoint = requiredPoint
        this.description = description
        this.dateApplied = dateApplied
        this.dateExpired = dateApplied
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}
