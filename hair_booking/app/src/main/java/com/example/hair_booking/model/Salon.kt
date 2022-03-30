package com.example.hair_booking.model

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Salon(private val _id: String) {
    private var _name: String? = null
    private var _avatar: String? = null
    private var _description: String? = null
    private var _rate: Long? = null
    private var _openHour: String? = null
    private var _closeHour: String? = null


    // "*" data type is used to deal with map in map in firestore
    private var _address: HashMap<String, String>? = null
    private var _appointments: ArrayList<HashMap<String, *>>?  = null
    private var _stylists: ArrayList<HashMap<String, *>>? = null


    // GETTERS
    val id: String get() = _id
    val name: String? get() = _name
    val avatar: String? get() = _avatar
    val description: String? get() = _description
    val rate: Long? get() = _rate
    val openHour: String? get() = _openHour
    val closeHour: String? get() = _closeHour
    val address: HashMap<String, String>? get() = _address
    val appointments: ArrayList<HashMap<String, *>>?  get() = _appointments
    val stylists: ArrayList<HashMap<String, *>>? get() = _stylists


    // Full parameter constructor
    constructor(
        id: String,
        name: String,
        avatar: String,
        description: String,
        rate: Long,
        openHour: String,
        closeHour: String,
        address: HashMap<String, String>,
        appointments: ArrayList<HashMap<String, *>>,
        stylists: ArrayList<HashMap<String, *>>
    ): this(id) {
        this._name = name
        this._avatar = avatar
        this._description = description
        this._rate = rate
        this._openHour = openHour
        this._closeHour = closeHour
        this._address = address
        this._appointments = appointments
        this._stylists = stylists
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}