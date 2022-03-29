package com.example.hair_booking.model

// Primary constructor only contains 1 parameter because these following reason:
// - Secondary constructor in kotlin MUST call primary first,
// it means the secondary constructor must include the id
// and the id is a required parameter for all models
// => you can freely define secondary constructor with as many parameters as you want
data class Salon(private val id: String) {
    private var name: String? = null
    private var avatar: String? = null
    private var description: String? = null
    private var rate: Long? = null
    private var openHour: String? = null
    private var closeHour: String? = null


    // "*" data type is used to deal with map in map in firestore
    private var address: HashMap<String, String>? = null
    private var appointments: ArrayList<HashMap<String, *>>?  = null
    private var stylists: ArrayList<HashMap<String, *>>? = null



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
        this.name = name
        this.avatar = avatar
        this.description = description
        this.rate = rate
        this.openHour = openHour
        this.closeHour = closeHour
        this.address = address
        this.appointments = appointments
        this.stylists = stylists
    }

    // DEFINE YOUR CUSTOM SECONDARY CONSTRUCTORS BELOW
}