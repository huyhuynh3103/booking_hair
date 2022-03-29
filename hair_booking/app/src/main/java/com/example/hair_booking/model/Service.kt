package com.example.hair_booking.model

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Service(private val _id: String) {
    private var _title: String? = null
    private var _price: Long? = null
    private var _description: String? = null


    // GETTERS
    val id: String = _id
    val title: String? = _title
    val price: Long? = _price
    val description: String? = _description

    // Full parameter constructor
    constructor(
        id: String,
        fullName: String,
        price: Long,
        description: String
    ): this(id) {
        this._title = fullName
        this._price = price
        this._description = description
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}