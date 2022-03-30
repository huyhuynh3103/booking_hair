package com.example.hair_booking.model

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Discount(private val _id: String) {
    private var _title: String? = null
    private var _requiredPoint: Long? = null
    private var _description: String? = null
    private var _dateApplied: String? = null
    private var _dateExpired: String? = null


    // GETTERS
    val id: String get() = _id
    val title: String? get() = _title
    val requiredPoint: Long? get() = _requiredPoint
    val description: String? get() = _description
    val dateApplied: String? get() = _dateApplied
    val dateExpired: String? get() = _dateExpired

    // Full parameter constructor
    constructor(id: String,
                title: String,
                requiredPoint: Long,
                description: String,
                dateApplied: String,
                dateExpired: String
    ) : this(id) {
        this._title = title
        this._requiredPoint = requiredPoint
        this._description = description
        this._dateApplied = dateApplied
        this._dateExpired = dateApplied
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}
